package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.IbanPayment;
import bank.rest.app.bankrestapp.entity.InternetPayment;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.entity.enums.TransactionType;
import bank.rest.app.bankrestapp.exception.InvalidAccountCurrencyException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import bank.rest.app.bankrestapp.exception.UnsupportedCurrencyException;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.PaymentRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Objects;

import static bank.rest.app.bankrestapp.entity.enums.PaymentStatus.COMPLETED;
import static java.time.LocalDateTime.now;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final EnumSet<Currency> SUPPORTED_IBAN_CURRENCIES = EnumSet.of(Currency.UAH, Currency.USD, Currency.EUR);

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyLoader currencyLoader;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment processIbanPayment(final IbanPaymentRequestDTO request, final String authenticatedUserEmail) {
        if (!request.recipientIban().startsWith("UA")) {
            throw new IllegalArgumentException("Recipient IBAN must start with UA");
        }

        final Account senderAccount = getValidOwnedAccount(request.accountId(), authenticatedUserEmail);
        validateIbanSupportedCurrency(senderAccount.getCurrencyCode());

        final BigDecimal originalAmount = request.amount();
        validateSufficientFunds(senderAccount, originalAmount);
        final BigDecimal amountInUah;
        if (!Currency.UAH.equals(senderAccount.getCurrencyCode())) {
            final CurrencyLoader.CurrencyRate exchangeRate = this.currencyLoader.getRate(senderAccount.getCurrencyCode().name())
                    .orElseThrow(() -> new UnsupportedCurrencyException("Не найден курс для: " + senderAccount.getCurrencyCode().name()));
            amountInUah = originalAmount.multiply(BigDecimal.valueOf(exchangeRate.getRate()));
        } else {
            amountInUah = originalAmount;
        }

        final Account recipient = this.accountRepository.findByAccountNumber(request.recipientIban())
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
        senderAccount.setBalance(senderAccount.getBalance().subtract(originalAmount));
        recipient.setBalance(recipient.getBalance().add(amountInUah));
        this.accountRepository.save(senderAccount);
        this.accountRepository.save(recipient);

        final IbanPayment payment = new IbanPayment();
        payment.setAccount(senderAccount);
        payment.setAmount(request.amount());
        payment.setCurrencyCode(senderAccount.getCurrencyCode().name());
        payment.setPaymentDate(now());
        payment.setStatus(COMPLETED);
        payment.setBeneficiaryName(request.recipientName());
        payment.setBeneficiaryAcc(request.recipientIban());
        payment.setTaxNumber(request.taxNumber());
        payment.setPurpose(request.purpose());
        payment.setRecipientName(request.recipientName());
        payment.setRecipientIban(request.recipientIban());
        payment.setTransaction(createTransaction(
                senderAccount,
                recipient,
                originalAmount,
                TransactionType.IBAN_PAYMENT,
                "Переказ за IBAN: " + request.recipientIban()
                        + ". До зарахування: "
                        + amountInUah.setScale(2, RoundingMode.HALF_UP)
                        + " UAH"
        ));

        return this.paymentRepository.save(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment processInternetPayment(final InternetPaymentRequestDTO request, final String authenticatedUserEmail) {
        final Account account = getValidOwnedAccount(request.accountId(), authenticatedUserEmail);
        validateInternetCurrency(account.getCurrencyCode());
        validateSufficientFunds(account, request.amount());
        account.setBalance(account.getBalance().subtract(request.amount()));
        this.accountRepository.save(account);

        final InternetPayment payment = new InternetPayment();
        payment.setAccount(account);
        payment.setAmount(request.amount());
        payment.setCurrencyCode(account.getCurrencyCode().name());
        payment.setPaymentDate(now());
        payment.setStatus(COMPLETED);
        payment.setBeneficiaryName(request.providerName());
        payment.setBeneficiaryAcc(request.contractNumber());
        payment.setProviderName(request.providerName());
        payment.setContractNumber(request.contractNumber());
        payment.setPurpose("Оплата послуг інтернет, провайдер: "
                + request.providerName()
                + ", дог. "
                + request.contractNumber());
        payment.setTransaction(createTransaction(
                account,
                null,
                request.amount(),
                TransactionType.INTERNET_PAYMENT,
                "Оплата інтернету (провайдер: " + request.providerName() + ")"
        ));

        return this.paymentRepository.save(payment);
    }

    private Transaction createTransaction(final Account senderAccount,
                                          final Account recipientAccount,
                                          final BigDecimal amount,
                                          final TransactionType transactionType,
                                          final String description) {
        final Transaction transaction = Transaction.builder()
                .amount(amount)
                .currencyCode(senderAccount.getCurrencyCode())
                .transactionType(transactionType)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .transactionDate(now())
                .account(senderAccount)
                .toAccount(recipientAccount)
                .build();

        return this.transactionRepository.save(transaction);
    }

    private Account getValidOwnedAccount(final Long accountId, final String authenticatedUserEmail) {
        final Integer id;
        try {
            id = Math.toIntExact(accountId);
        } catch (ArithmeticException ex) {
            throw new NoSuchElementException("Account not found");
        }

        final Account account = this.accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));

        if (account.getCustomer() == null
                || account.getCustomer().getAuthUser() == null
                || !Objects.equals(account.getCustomer().getAuthUser().getEmail(), authenticatedUserEmail)) {
            throw new IllegalArgumentException("Account does not belong to the authenticated user");
        }

        return account;
    }

    private void validateInternetCurrency(final Currency currency) {
        if (!Currency.UAH.equals(currency)) {
            throw new InvalidAccountCurrencyException("Payments are allowed only from UAH accounts");
        }
    }

    private void validateIbanSupportedCurrency(final Currency currency) {
        if (currency == null || !SUPPORTED_IBAN_CURRENCIES.contains(currency)) {
            throw new UnsupportedCurrencyException("Unsupported account currency for IBAN payment");
        }
    }

    private void validateSufficientFunds(final Account account, final BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account");
        }
    }
}
