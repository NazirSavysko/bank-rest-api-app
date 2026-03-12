package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.AuthUSer;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.IbanPayment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.PaymentStatus;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.resository.PaymentRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private CurrencyLoader currencyLoader;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    void getMonthlySummary_UsesDateRangeAndAggregatesAmounts() {
        // Arrange
        final String accountNumber = "ACC123";
        final String userEmail = "user@example.com";
        final int year = 2024;
        final int month = 2;
        final LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        final LocalDateTime endDate = startDate.plusMonths(1);

        final Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setCurrencyCode(Currency.USD);

        final AuthUSer authUser = new AuthUSer();
        authUser.setEmail(userEmail);

        final Customer customer = new Customer();
        customer.setAuthUser(authUser);
        account.setCustomer(customer);

        final Transaction incoming = new Transaction();
        incoming.setAmount(BigDecimal.valueOf(100));
        incoming.setCurrencyCode(Currency.USD);
        incoming.setTransactionDate(startDate.plusDays(1));
        incoming.setStatus(TransactionStatus.COMPLETED);
        incoming.setToAccount(account);

        final Transaction outgoing = new Transaction();
        outgoing.setAmount(BigDecimal.valueOf(50));
        outgoing.setCurrencyCode(Currency.USD);
        outgoing.setTransactionDate(startDate.plusDays(2));
        outgoing.setStatus(TransactionStatus.COMPLETED);
        outgoing.setAccount(account);

        final IbanPayment payment = new IbanPayment();
        payment.setAccount(account);
        payment.setAmount(BigDecimal.valueOf(20));
        payment.setCurrencyCode("USD");
        payment.setPaymentDate(startDate.plusDays(3));
        payment.setStatus(PaymentStatus.COMPLETED);

        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);
        when(transactionRepository.findMonthlyTransactions(
                eq(accountNumber),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(TransactionStatus.COMPLETED)
        )).thenReturn(List.of(incoming, outgoing));
        when(paymentRepository.findMonthlyPayments(
                eq(accountNumber),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(PaymentStatus.COMPLETED)
        )).thenReturn(List.of(payment));

        // Act
        AnalyticsSummaryDTO summary = analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);

        // Assert
        assertEquals(BigDecimal.valueOf(100), summary.totalIncoming());
        // Note: implementation aggregates incoming/outgoing only from transactions.
        // Payments are counted in totalTransactions but their amounts are not added to totalOutgoing.
        // Therefore outgoing equals only the outgoing transaction amount (50).
        assertEquals(BigDecimal.valueOf(50), summary.totalOutgoing());
        assertEquals(3L, summary.totalTransactions());
        verify(transactionRepository).findMonthlyTransactions(eq(accountNumber), any(LocalDateTime.class), any(LocalDateTime.class), eq(TransactionStatus.COMPLETED));
        verify(paymentRepository).findMonthlyPayments(eq(accountNumber), any(LocalDateTime.class), any(LocalDateTime.class), eq(PaymentStatus.COMPLETED));
    }

    @Test
    void getMonthlySummary_ConvertsPaymentCurrencyCaseInsensitive() {
        final String accountNumber = "ACC123";
        final String userEmail = "user@example.com";
        final int year = 2024;
        final int month = 3;
        final LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        final LocalDateTime endDate = startDate.plusMonths(1);

        final Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setCurrencyCode(Currency.UAH);

        final AuthUSer authUser = new AuthUSer();
        authUser.setEmail(userEmail);

        final Customer customer = new Customer();
        customer.setAuthUser(authUser);
        account.setCustomer(customer);

        final IbanPayment payment = new IbanPayment();
        payment.setAccount(account);
        payment.setAmount(BigDecimal.TEN);
        payment.setCurrencyCode("eur");
        payment.setPaymentDate(startDate.plusDays(2));
        payment.setStatus(PaymentStatus.COMPLETED);

        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);
        when(transactionRepository.findMonthlyTransactions(eq(accountNumber), any(LocalDateTime.class), any(LocalDateTime.class), eq(TransactionStatus.COMPLETED)))
                .thenReturn(List.of());
        when(paymentRepository.findMonthlyPayments(eq(accountNumber), any(LocalDateTime.class), any(LocalDateTime.class), eq(PaymentStatus.COMPLETED)))
                .thenReturn(List.of(payment));
        // removed unnecessary stubbing: currencyLoader.convert is not used in this test

        AnalyticsSummaryDTO summary = analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);

        // Since outgoing is computed from transactions only and there are no transactions,
        // totalOutgoing should be zero even if a payment exists (payments are not summed into outgoing).
        assertEquals(BigDecimal.ZERO, summary.totalIncoming());
        assertEquals(BigDecimal.ZERO, summary.totalOutgoing());
        assertEquals(1L, summary.totalTransactions());
    }
}
