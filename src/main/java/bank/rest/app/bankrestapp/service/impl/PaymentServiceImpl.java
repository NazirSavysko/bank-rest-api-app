package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.IbanPayment;
import bank.rest.app.bankrestapp.entity.InternetPayment;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.AccountType;
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

import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_NOT_FOUND;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_OWNERSHIP_MISMATCH;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_EXCHANGE_RATE_NOT_FOUND;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_FOP_ACCOUNT_EDRPOU_REQUIRED;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_INSUFFICIENT_FUNDS;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_INVALID_RECIPIENT_IBAN;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_PAYMENTS_ALLOWED_ONLY_FROM_UAH_ACCOUNTS;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_UNSUPPORTED_ACCOUNT_CURRENCY_FOR_IBAN_PAYMENT;
import static bank.rest.app.bankrestapp.entity.enums.PaymentStatus.COMPLETED;
import static java.lang.String.format;
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
        this.validateRecipientIban(request.recipientIban());

        final Account senderAccount = this.getValidOwnedAccount(request.accountId(), authenticatedUserEmail);
        this.validateIbanPaymentAccount(senderAccount, request.amount());

        final BigDecimal amountInUah = this.resolveAmountInUah(senderAccount, request.amount());
        this.debitAccount(senderAccount, request.amount());

        final IbanPayment payment = this.buildIbanPayment(request, senderAccount);
        payment.setTransaction(this.createTransaction(
                senderAccount,
                null,
                request.amount(),
                TransactionType.IBAN_PAYMENT,
                this.buildIbanDescription(request.recipientIban(), amountInUah)
        ));

        return this.paymentRepository.save(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment processInternetPayment(final InternetPaymentRequestDTO request, final String authenticatedUserEmail) {
        final Account account = this.getValidOwnedAccount(request.accountId(), authenticatedUserEmail);

        this.validateInternetPaymentAccount(account, request.amount());
        this.debitAccount(account, request.amount());

        final InternetPayment payment = this.buildInternetPayment(request, account);
        payment.setTransaction(this.createTransaction(
                account,
                null,
                request.amount(),
                TransactionType.INTERNET_PAYMENT,
                "Оплата інтернету (провайдер: " + request.providerName() + ")"
        ));

        return this.paymentRepository.save(payment);
    }

    private void validateIbanPaymentAccount(final Account senderAccount, final BigDecimal amount) {
        this.validateFopAccount(senderAccount);
        this.validateIbanSupportedCurrency(senderAccount.getCurrencyCode());
        this.validateSufficientFunds(senderAccount, amount);
    }

    private void validateInternetPaymentAccount(final Account account, final BigDecimal amount) {
        this.validateInternetCurrency(account.getCurrencyCode());
        this.validateSufficientFunds(account, amount);
    }

    private BigDecimal resolveAmountInUah(final Account senderAccount, final BigDecimal originalAmount) {
        if (Currency.UAH.equals(senderAccount.getCurrencyCode())) {
            return originalAmount;
        }

        final CurrencyLoader.CurrencyRate exchangeRate = this.currencyLoader.getRate(senderAccount.getCurrencyCode().name())
                .orElseThrow(() -> new UnsupportedCurrencyException(
                        format(ERRORS_EXCHANGE_RATE_NOT_FOUND, senderAccount.getCurrencyCode().name())
                ));

        return originalAmount.multiply(BigDecimal.valueOf(exchangeRate.getRate()));
    }

    private void debitAccount(final Account account, final BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
        this.accountRepository.save(account);
    }

    private IbanPayment buildIbanPayment(final IbanPaymentRequestDTO request, final Account senderAccount) {
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

        return payment;
    }

    private String buildIbanDescription(final String recipientIban, final BigDecimal amountInUah) {
        return "Переказ за IBAN: "
                + recipientIban
                + ". До зарахування: "
                + amountInUah.setScale(2, RoundingMode.HALF_UP)
                + " UAH";
    }

    private InternetPayment buildInternetPayment(final InternetPaymentRequestDTO request, final Account account) {
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

        return payment;
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
        final Integer id = this.convertAccountId(accountId);
        final Account account = this.accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_ACCOUNT_NOT_FOUND));

        if (account.getCustomer() == null
                || account.getCustomer().getAuthUser() == null
                || !Objects.equals(account.getCustomer().getAuthUser().getEmail(), authenticatedUserEmail)) {
            throw new IllegalArgumentException(ERRORS_ACCOUNT_OWNERSHIP_MISMATCH);
        }

        return account;
    }

    private Integer convertAccountId(final Long accountId) {
        try {
            return Math.toIntExact(accountId);
        } catch (ArithmeticException ex) {
            throw new NoSuchElementException(ERRORS_ACCOUNT_NOT_FOUND);
        }
    }

    private void validateInternetCurrency(final Currency currency) {
        if (!Currency.UAH.equals(currency)) {
            throw new InvalidAccountCurrencyException(ERRORS_PAYMENTS_ALLOWED_ONLY_FROM_UAH_ACCOUNTS);
        }
    }

    private void validateIbanSupportedCurrency(final Currency currency) {
        if (currency == null || !SUPPORTED_IBAN_CURRENCIES.contains(currency)) {
            throw new UnsupportedCurrencyException(ERRORS_UNSUPPORTED_ACCOUNT_CURRENCY_FOR_IBAN_PAYMENT);
        }
    }

    private void validateRecipientIban(final String recipientIban) {
        if (recipientIban == null
                || recipientIban.length() != 34
                || !recipientIban.startsWith("UA")
                || !recipientIban.substring(2).chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException(ERRORS_INVALID_RECIPIENT_IBAN);
        }
    }

    private void validateFopAccount(final Account account) {
        if (AccountType.FOP.equals(account.getAccountType())
                && (account.getEdrpou() == null || account.getEdrpou().isBlank())) {
            throw new IllegalStateException(ERRORS_FOP_ACCOUNT_EDRPOU_REQUIRED);
        }
    }

    private void validateSufficientFunds(final Account account, final BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(ERRORS_INSUFFICIENT_FUNDS);
        }
    }
}
