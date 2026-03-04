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
        if (accountNumber == null || year == null || month == null) {
            throw new IllegalArgumentException("accountNumber, year and month are required");
        }

        final Account account = this.accountService.getAccountByNumber(accountNumber);

        if (userEmail != null && (account.getCustomer() == null
                || account.getCustomer().getAuthUser() == null
                || !Objects.equals(account.getCustomer().getAuthUser().getEmail(), userEmail))) {
            throw new IllegalArgumentException("Account does not belong to the authenticated user");
        }

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

        BigDecimal incoming = BigDecimal.ZERO;
        BigDecimal outgoing = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            final BigDecimal normalizedAmount = normalizeAmount(transaction, account.getCurrencyCode());

            if (transaction.getToAccount() != null
                    && accountNumber.equals(transaction.getToAccount().getAccountNumber())) {
                incoming = incoming.add(normalizedAmount);
            }

            if (transaction.getAccount() != null
                    && accountNumber.equals(transaction.getAccount().getAccountNumber())) {
                outgoing = outgoing.add(normalizedAmount);
            }
        }

        for (Payment payment : payments) {
            outgoing = outgoing.add(normalizeAmount(payment, account.getCurrencyCode()));
        }

        return new AnalyticsSummaryDTO(incoming, outgoing, transactions.size() + payments.size());
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

        final Currency paymentCurrency;
        try {
            paymentCurrency = Currency.valueOf(payment.getCurrencyCode().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return amount;
        }

        if (paymentCurrency.equals(targetCurrency)) {
            return amount;
        }

        return this.currencyLoader.convert(amount, paymentCurrency.name(), targetCurrency.name());
    }
}
