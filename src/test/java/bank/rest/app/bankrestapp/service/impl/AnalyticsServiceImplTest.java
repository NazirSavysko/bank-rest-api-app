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

//    @Test
//    void getMonthlySummary_UsesDateRangeAndAggregatesAmounts() {
//        // Arrange
//        final String accountNumber = "ACC123";
//        final String userEmail = "user@example.com";
//        final int year = 2024;
//        final int month = 2;
//        final LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
//        final LocalDateTime endDate = startDate.plusMonths(1);
//
//        final Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setCurrencyCode(Currency.USD);
//
//        final AuthUSer authUser = new AuthUSer();
//        authUser.setEmail(userEmail);
//
//        final Customer customer = new Customer();
//        customer.setAuthUser(authUser);
//        account.setCustomer(customer);
//
//        final Transaction incoming = new Transaction();
//        incoming.setAmount(BigDecimal.valueOf(100));
//        incoming.setCurrencyCode(Currency.USD);
//        incoming.setTransactionDate(startDate.plusDays(1));
//        incoming.setStatus(TransactionStatus.COMPLETED);
//        incoming.setToAccount(account);
//
//        final Transaction outgoing = new Transaction();
//        outgoing.setAmount(BigDecimal.valueOf(50));
//        outgoing.setCurrencyCode(Currency.USD);
//        outgoing.setTransactionDate(startDate.plusDays(2));
//        outgoing.setStatus(TransactionStatus.COMPLETED);
//        outgoing.setAccount(account);
//
//        final IbanPayment payment = new IbanPayment();
//        payment.setAccount(account);
//        payment.setAmount(BigDecimal.valueOf(20));
//        payment.setCurrencyCode("USD");
//        payment.setPaymentDate(startDate.plusDays(3));
//        payment.setStatus(PaymentStatus.COMPLETED);
//
//        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);
//        when(transactionRepository.findMonthlyTransactions(
//                eq(accountNumber),
//                eq(startDate),
//                eq(endDate),
//                eq(TransactionStatus.COMPLETED)
//        )).thenReturn(List.of(incoming, outgoing));
//        when(paymentRepository.findMonthlyPayments(
//                eq(accountNumber),
//                eq(startDate),
//                eq(endDate),
//                eq(PaymentStatus.COMPLETED)
//        )).thenReturn(List.of(payment));
//
//        // Act
//        AnalyticsSummaryDTO summary = analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);
//
//        // Assert
//        assertEquals(BigDecimal.valueOf(100), summary.totalIncoming());
//        assertEquals(BigDecimal.valueOf(70), summary.totalOutgoing());
//        assertEquals(3L, summary.totalTransactions());
//        verify(transactionRepository).findMonthlyTransactions(accountNumber, startDate, endDate, TransactionStatus.COMPLETED);
//        verify(paymentRepository).findMonthlyPayments(accountNumber, startDate, endDate, PaymentStatus.COMPLETED);
//    }
//
//    @Test
//    void getMonthlySummary_ConvertsPaymentCurrencyCaseInsensitive() {
//        final String accountNumber = "ACC123";
//        final String userEmail = "user@example.com";
//        final int year = 2024;
//        final int month = 3;
//        final LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
//        final LocalDateTime endDate = startDate.plusMonths(1);
//
//        final Account account = new Account();
//        account.setAccountNumber(accountNumber);
//        account.setCurrencyCode(Currency.UAH);
//
//        final AuthUSer authUser = new AuthUSer();
//        authUser.setEmail(userEmail);
//
//        final Customer customer = new Customer();
//        customer.setAuthUser(authUser);
//        account.setCustomer(customer);
//
//        final IbanPayment payment = new IbanPayment();
//        payment.setAccount(account);
//        payment.setAmount(BigDecimal.TEN);
//        payment.setCurrencyCode("eur");
//        payment.setPaymentDate(startDate.plusDays(2));
//        payment.setStatus(PaymentStatus.COMPLETED);
//
//        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);
//        when(transactionRepository.findMonthlyTransactions(accountNumber, startDate, endDate, TransactionStatus.COMPLETED))
//                .thenReturn(List.of());
//        when(paymentRepository.findMonthlyPayments(accountNumber, startDate, endDate, PaymentStatus.COMPLETED))
//                .thenReturn(List.of(payment));
//        when(currencyLoader.convert(BigDecimal.TEN, "EUR", "UAH")).thenReturn(BigDecimal.valueOf(420));
//
//        AnalyticsSummaryDTO summary = analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);
//
//        assertEquals(BigDecimal.ZERO, summary.totalIncoming());
//        assertEquals(BigDecimal.valueOf(420), summary.totalOutgoing());
//        assertEquals(1L, summary.totalTransactions());
//    }
}
