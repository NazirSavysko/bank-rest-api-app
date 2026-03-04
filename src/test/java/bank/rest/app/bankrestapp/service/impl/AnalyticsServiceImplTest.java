package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    void getMonthlySummary_returnsAggregatedData() {
        final Account account = new Account();
        account.setAccountId(1040);
        account.setCurrencyCode(Currency.UAH);

        when(accountRepository.findByAccountIdAndCustomerAuthUserEmail(1040, "user@example.com"))
                .thenReturn(Optional.of(account));

        final TransactionRepository.TransactionSummary summary = new StubSummary(
                new BigDecimal("15400.50"),
                new BigDecimal("8200.00"),
                24L
        );

        when(transactionRepository.getMonthlySummary(eq(1040), anyList(), any(), any()))
                .thenReturn(summary);

        final AnalyticsSummaryDTO result = analyticsService.getMonthlySummary(1040L, 2026, 3, "user@example.com");

        assertEquals(1040L, result.accountId());
        assertEquals(2026, result.year());
        assertEquals(3, result.month());
        assertEquals(new BigDecimal("15400.50"), result.totalIncome());
        assertEquals(new BigDecimal("8200.00"), result.totalExpense());
        assertEquals(24L, result.operationsCount());
        assertEquals("UAH", result.currency());
        verify(transactionRepository).getMonthlySummary(eq(1040), anyList(), any(), any());
    }

    @Test
    void getMonthlySummary_returnsZerosWhenNoTransactions() {
        final Account account = new Account();
        account.setAccountId(1040);
        account.setCurrencyCode(Currency.UAH);

        when(accountRepository.findByAccountIdAndCustomerAuthUserEmail(1040, "user@example.com"))
                .thenReturn(Optional.of(account));
        when(transactionRepository.getMonthlySummary(eq(1040), anyList(), any(), any()))
                .thenReturn(null);

        final AnalyticsSummaryDTO result = analyticsService.getMonthlySummary(1040L, 2026, 3, "user@example.com");

        assertEquals(BigDecimal.ZERO, result.totalIncome());
        assertEquals(BigDecimal.ZERO, result.totalExpense());
        assertEquals(0L, result.operationsCount());
        assertEquals("UAH", result.currency());
    }

    @Test
    void getMonthlySummary_throwsAccessDeniedWhenAccountNotOwned() {
        when(accountRepository.findByAccountIdAndCustomerAuthUserEmail(1040, "user@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccessDeniedException.class,
                () -> analyticsService.getMonthlySummary(1040L, 2026, 3, "user@example.com")
        );

        verify(transactionRepository, never()).getMonthlySummary(any(), anyList(), any(), any());
    }

    private record StubSummary(BigDecimal totalIncome,
                               BigDecimal totalExpense,
                               long operationsCount) implements TransactionRepository.TransactionSummary {

        @Override
        public BigDecimal getTotalIncome() {
            return totalIncome;
        }

        @Override
        public BigDecimal getTotalExpense() {
            return totalExpense;
        }

        @Override
        public long getOperationsCount() {
            return operationsCount;
        }
    }
}
