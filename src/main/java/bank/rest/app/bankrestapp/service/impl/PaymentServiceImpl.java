package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.IbanPayment;
import bank.rest.app.bankrestapp.entity.InternetPayment;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.exception.InvalidAccountCurrencyException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.PaymentRepository;
import bank.rest.app.bankrestapp.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;

import static bank.rest.app.bankrestapp.entity.enums.PaymentStatus.COMPLETED;
import static java.time.LocalDateTime.now;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Payment processIbanPayment(final IbanPaymentRequestDTO request, final String authenticatedUserEmail) {
        if (!request.recipientIban().startsWith("UA")) {
            throw new IllegalArgumentException("Recipient IBAN must start with UA");
        }

        final Account account = getValidOwnedUahAccount(request.accountId(), authenticatedUserEmail);
        validateSufficientFunds(account, request.amount());
        account.setBalance(account.getBalance().subtract(request.amount()));
        this.accountRepository.save(account);

        final IbanPayment payment = new IbanPayment();
        payment.setAccount(account);
        payment.setAmount(request.amount());
        payment.setCurrencyCode(account.getCurrencyCode().name());
        payment.setPaymentDate(now());
        payment.setStatus(COMPLETED);
        payment.setBeneficiaryName(request.recipientName());
        payment.setBeneficiaryAcc(request.recipientIban());
        payment.setTaxNumber(request.taxNumber());
        payment.setPurpose(request.purpose());
        payment.setRecipientName(request.recipientName());
        payment.setRecipientIban(request.recipientIban());

        return this.paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment processInternetPayment(final InternetPaymentRequestDTO request, final String authenticatedUserEmail) {
        final Account account = getValidOwnedUahAccount(request.accountId(), authenticatedUserEmail);
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

        return this.paymentRepository.save(payment);
    }

    private Account getValidOwnedUahAccount(final Long accountId, final String authenticatedUserEmail) {
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

        if (!Currency.UAH.equals(account.getCurrencyCode())) {
            throw new InvalidAccountCurrencyException("Payments are allowed only from UAH accounts");
        }

        return account;
    }

    private void validateSufficientFunds(final Account account, final java.math.BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account");
        }
    }
}
