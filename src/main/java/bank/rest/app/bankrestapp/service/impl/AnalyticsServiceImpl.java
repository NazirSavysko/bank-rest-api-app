package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.AnalyticsService;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TransactionRepository transactionRepository;
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

        final List<Transaction> transactions = this.transactionRepository.findMonthlyTransactions(
                accountNumber,
                year,
                month,
                TransactionStatus.COMPLETED
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

        return new AnalyticsSummaryDTO(incoming, outgoing, transactions.size());
    }

    private BigDecimal normalizeAmount(final Transaction transaction, final Currency targetCurrency) {
        final Currency transactionCurrency = transaction.getCurrencyCode();
        final BigDecimal amount = transaction.getAmount() == null ? BigDecimal.ZERO : transaction.getAmount();

        if (transactionCurrency == null || targetCurrency == null || transactionCurrency.equals(targetCurrency)) {
            return amount;
        }

        return this.currencyLoader.convert(amount, transactionCurrency.name(), targetCurrency.name());
    }
}
