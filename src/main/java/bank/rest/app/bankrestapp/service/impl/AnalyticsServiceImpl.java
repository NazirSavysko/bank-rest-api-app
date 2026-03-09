package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.PaymentStatus;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.resository.PaymentRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.AnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_OWNERSHIP_MISMATCH;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_REQUIRED_ANALYTICS_PARAMETERS;

@Service
@AllArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final CurrencyLoader currencyLoader;

    @Override
    public AnalyticsSummaryDTO getMonthlySummary(final String accountNumber,
                                                 final Integer year,
                                                 final Integer month,
                                                 final String userEmail) {
        this.validateAnalyticsParameters(accountNumber, year, month);

        final Account account = this.accountService.getAccountByNumber(accountNumber);
        this.validateOwnership(account, userEmail);

        final LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        final LocalDateTime endDate = startDate.plusMonths(1);

        final List<Transaction> transactions = this.transactionRepository.findMonthlyTransactions(
                accountNumber,
                startDate,
                endDate,
                TransactionStatus.COMPLETED
        );
        final List<Payment> payments = this.paymentRepository.findMonthlyPayments(
                accountNumber,
                startDate,
                endDate,
                PaymentStatus.COMPLETED
        );

        final BigDecimal incoming = transactions.stream()
                .filter(transaction -> transaction.getToAccount() != null
                        && accountNumber.equals(transaction.getToAccount().getAccountNumber()))
                .map(transaction -> this.normalizeAmount(transaction, account.getCurrencyCode()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal outgoingTransactions = transactions.stream()
                .filter(transaction -> transaction.getAccount() != null
                        && accountNumber.equals(transaction.getAccount().getAccountNumber()))
                .map(transaction -> this.normalizeAmount(transaction, account.getCurrencyCode()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal outgoingPayments = payments.stream()
                .map(payment -> this.normalizeAmount(payment, account.getCurrencyCode()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AnalyticsSummaryDTO(
                incoming,
                outgoingTransactions.add(outgoingPayments),
                transactions.size() + payments.size()
        );
    }

    private void validateAnalyticsParameters(final String accountNumber, final Integer year, final Integer month) {
        if (accountNumber == null || year == null || month == null) {
            throw new IllegalArgumentException(ERRORS_REQUIRED_ANALYTICS_PARAMETERS);
        }
    }

    private void validateOwnership(final Account account, final String userEmail) {
        if (userEmail != null
                && (account.getCustomer() == null
                || account.getCustomer().getAuthUser() == null
                || !Objects.equals(account.getCustomer().getAuthUser().getEmail(), userEmail))) {
            throw new IllegalArgumentException(ERRORS_ACCOUNT_OWNERSHIP_MISMATCH);
        }
    }

    private BigDecimal normalizeAmount(final Transaction transaction, final Currency targetCurrency) {
        final Currency transactionCurrency = transaction.getCurrencyCode();
        final BigDecimal amount = transaction.getAmount() == null ? BigDecimal.ZERO : transaction.getAmount();

        if (transactionCurrency == null || targetCurrency == null || transactionCurrency.equals(targetCurrency)) {
            return amount;
        }

        return this.currencyLoader.convert(amount, transactionCurrency.name(), targetCurrency.name());
    }

    private BigDecimal normalizeAmount(final Payment payment, final Currency targetCurrency) {
        final BigDecimal amount = payment.getAmount() == null ? BigDecimal.ZERO : payment.getAmount();

        if (payment.getCurrencyCode() == null || targetCurrency == null) {
            return amount;
        }

        final Currency paymentCurrency = Currency.valueOf(payment.getCurrencyCode().toUpperCase(Locale.ROOT));
        if (paymentCurrency.equals(targetCurrency)) {
            return amount;
        }

        return this.currencyLoader.convert(amount, paymentCurrency.name(), targetCurrency.name());
    }
}
