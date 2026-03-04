package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.service.AnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

@Service
@AllArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public AnalyticsSummaryDTO getMonthlySummary(final Long accountId,
                                                 final Integer year,
                                                 final Integer month,
                                                 final String userEmail) {
        final Integer normalizedAccountId = Math.toIntExact(accountId);

        final Account account = this.accountRepository
                .findByAccountIdAndCustomerAuthUserEmail(normalizedAccountId, userEmail)
                .orElseThrow(() -> new AccessDeniedException("Access to the requested account is denied"));

        final LocalDateTime startDate = LocalDate.of(year, month, 1).atStartOfDay();
        final LocalDateTime endDate = startDate.plusMonths(1);

        final TransactionRepository.TransactionSummary summary = this.transactionRepository.getMonthlySummary(
                normalizedAccountId,
                List.of(TransactionStatus.CANCELLED, TransactionStatus.FAILED),
                startDate,
                endDate
        );

        final BigDecimal totalIncome = Optional.ofNullable(summary)
                .map(TransactionRepository.TransactionSummary::getTotalIncome)
                .orElse(ZERO);

        final BigDecimal totalExpense = Optional.ofNullable(summary)
                .map(TransactionRepository.TransactionSummary::getTotalExpense)
                .orElse(ZERO);

        final long operationsCount = Optional.ofNullable(summary)
                .map(TransactionRepository.TransactionSummary::getOperationsCount)
                .orElse(0L);

        return new AnalyticsSummaryDTO(
                account.getAccountId().longValue(),
                year,
                month,
                totalIncome,
                totalExpense,
                operationsCount,
                account.getCurrencyCode().name()
        );
    }
}
