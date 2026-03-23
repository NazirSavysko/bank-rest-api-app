package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.AuthUSer;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.ElectronicsPayment;
import bank.rest.app.bankrestapp.entity.IbanPayment;
import bank.rest.app.bankrestapp.entity.InternetPayment;
import bank.rest.app.bankrestapp.entity.MobilePayment;
import bank.rest.app.bankrestapp.entity.TaxPayment;
import bank.rest.app.bankrestapp.entity.TrainPayment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.PaymentStatus;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.entity.enums.TransactionType;
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

//    @Test
//    void getMonthlySummary_ShouldAggregateCategoryExpenses() {
//        final String accountNumber = "ACC123";
//        final String userEmail = "user@example.com";
//        final int year = 2024;
//        final int month = 2;
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
//        final Transaction incoming = new Transaction();
//        incoming.setAmount(BigDecimal.valueOf(100));
//        incoming.setCurrencyCode(Currency.UAH);
//        incoming.setStatus(TransactionStatus.COMPLETED);
//        incoming.setToAccount(account);
//
//        final Transaction transferOutgoing = new Transaction();
//        transferOutgoing.setAmount(BigDecimal.valueOf(40));
//        transferOutgoing.setCurrencyCode(Currency.UAH);
//        transferOutgoing.setStatus(TransactionStatus.COMPLETED);
//        transferOutgoing.setAccount(account);
//        transferOutgoing.setTransactionType(TransactionType.TRANSFER);
//
//        final IbanPayment ibanPayment = new IbanPayment();
//        ibanPayment.setAccount(account);
//        ibanPayment.setAmount(BigDecimal.valueOf(20));
//        ibanPayment.setCurrencyCode("UAH");
//        ibanPayment.setStatus(PaymentStatus.COMPLETED);
//
//        final MobilePayment mobilePayment = new MobilePayment();
//        mobilePayment.setAccount(account);
//        mobilePayment.setAmount(BigDecimal.valueOf(15));
//        mobilePayment.setCurrencyCode("UAH");
//        mobilePayment.setStatus(PaymentStatus.COMPLETED);
//
//        final InternetPayment internetPayment = new InternetPayment();
//        internetPayment.setAccount(account);
//        internetPayment.setAmount(BigDecimal.valueOf(25));
//        internetPayment.setCurrencyCode("UAH");
//        internetPayment.setStatus(PaymentStatus.COMPLETED);
//
//        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);
//        when(transactionRepository.findMonthlyTransactions(
//                eq(accountNumber),
//                eq(startDate),
//                eq(endDate),
//                eq(TransactionStatus.COMPLETED)
//        )).thenReturn(List.of(incoming, transferOutgoing));
//        when(paymentRepository.findMonthlyPayments(
//                eq(accountNumber),
//                eq(startDate),
//                eq(endDate),
//                eq(PaymentStatus.COMPLETED)
//        )).thenReturn(List.of(ibanPayment, mobilePayment, internetPayment));
//
//        final AnalyticsSummaryDTO summary = analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);
//
//        assertEquals(BigDecimal.valueOf(100), summary.totalIncoming());
//        assertEquals(BigDecimal.valueOf(40), summary.totalOutgoing());
//        assertEquals(5L, summary.totalTransactions());
//        assertEquals(BigDecimal.valueOf(20), summary.totalIbanExpenses());
//        assertEquals(BigDecimal.valueOf(15), summary.totalMobileExpenses());
//        assertEquals(BigDecimal.valueOf(25), summary.totalInternetExpenses());
//        assertEquals(BigDecimal.valueOf(40), summary.totalCardToCardExpenses());
//        verify(transactionRepository).findMonthlyTransactions(accountNumber, startDate, endDate, TransactionStatus.COMPLETED);
//        verify(paymentRepository).findMonthlyPayments(accountNumber, startDate, endDate, PaymentStatus.COMPLETED);
//    }

    @Test
    void getMonthlySummary_ShouldAggregateTaxExpenses() {
        final String accountNumber = "ACC-TAX-123";
        final String userEmail = "user@example.com";
        final int year = 2026;
        final int month = 1;
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

        final TaxPayment taxPayment = new TaxPayment();
        taxPayment.setAccount(account);
        taxPayment.setAmount(BigDecimal.valueOf(150));
        taxPayment.setCurrencyCode("UAH");
        taxPayment.setStatus(PaymentStatus.COMPLETED);

        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);
        when(transactionRepository.findMonthlyTransactions(
                eq(accountNumber),
                eq(startDate),
                eq(endDate),
                eq(TransactionStatus.COMPLETED)
        )).thenReturn(List.of());
        when(paymentRepository.findMonthlyPayments(
                eq(accountNumber),
                eq(startDate),
                eq(endDate),
                eq(PaymentStatus.COMPLETED)
        )).thenReturn(List.of(taxPayment));

        final AnalyticsSummaryDTO summary = analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);

        assertEquals(BigDecimal.ZERO, summary.totalIncoming());
        assertEquals(BigDecimal.ZERO, summary.totalOutgoing());
        assertEquals(0L, summary.totalTransactions());
        assertEquals(BigDecimal.valueOf(150), summary.totalTaxExpenses());
        assertEquals(BigDecimal.ZERO, summary.totalElectronicsExpenses());
        verify(transactionRepository).findMonthlyTransactions(accountNumber, startDate, endDate, TransactionStatus.COMPLETED);
        verify(paymentRepository).findMonthlyPayments(accountNumber, startDate, endDate, PaymentStatus.COMPLETED);
    }

    @Test
    void getMonthlySummary_ShouldAggregateElectronicsExpenses() {
        final String accountNumber = "ACC-ELECTRONICS-123";
        final String userEmail = "user@example.com";
        final int year = 2026;
        final int month = 2;
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

        final ElectronicsPayment electronicsPayment = new ElectronicsPayment();
        electronicsPayment.setAccount(account);
        electronicsPayment.setAmount(BigDecimal.valueOf(9999));
        electronicsPayment.setCurrencyCode("UAH");
        electronicsPayment.setStatus(PaymentStatus.COMPLETED);

        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);
        when(transactionRepository.findMonthlyTransactions(
                eq(accountNumber),
                eq(startDate),
                eq(endDate),
                eq(TransactionStatus.COMPLETED)
        )).thenReturn(List.of());
        when(paymentRepository.findMonthlyPayments(
                eq(accountNumber),
                eq(startDate),
                eq(endDate),
                eq(PaymentStatus.COMPLETED)
        )).thenReturn(List.of(electronicsPayment));

        final AnalyticsSummaryDTO summary = analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);

        assertEquals(BigDecimal.ZERO, summary.totalIncoming());
        assertEquals(BigDecimal.ZERO, summary.totalOutgoing());
        assertEquals(0L, summary.totalTransactions());
        assertEquals(BigDecimal.ZERO, summary.totalTaxExpenses());
        assertEquals(BigDecimal.valueOf(9999), summary.totalElectronicsExpenses());
        verify(transactionRepository).findMonthlyTransactions(accountNumber, startDate, endDate, TransactionStatus.COMPLETED);
        verify(paymentRepository).findMonthlyPayments(accountNumber, startDate, endDate, PaymentStatus.COMPLETED);
    }

    @Test
    void getMonthlySummary_ShouldAggregateTravelExpenses() {
        final String accountNumber = "ACC-TRAVEL-123";
        final String userEmail = "user@example.com";
        final int year = 2026;
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

        final TrainPayment trainPayment = new TrainPayment();
        trainPayment.setAccount(account);
        trainPayment.setAmount(BigDecimal.valueOf(730));
        trainPayment.setCurrencyCode("UAH");
        trainPayment.setStatus(PaymentStatus.COMPLETED);

        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);
        when(transactionRepository.findMonthlyTransactions(
                eq(accountNumber),
                eq(startDate),
                eq(endDate),
                eq(TransactionStatus.COMPLETED)
        )).thenReturn(List.of());
        when(paymentRepository.findMonthlyPayments(
                eq(accountNumber),
                eq(startDate),
                eq(endDate),
                eq(PaymentStatus.COMPLETED)
        )).thenReturn(List.of(trainPayment));

        final AnalyticsSummaryDTO summary = analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);

        assertEquals(BigDecimal.ZERO, summary.totalIncoming());
        assertEquals(BigDecimal.ZERO, summary.totalOutgoing());
        assertEquals(0L, summary.totalTransactions());
        assertEquals(BigDecimal.ZERO, summary.totalTaxExpenses());
        assertEquals(BigDecimal.ZERO, summary.totalElectronicsExpenses());
        assertEquals(BigDecimal.valueOf(730), summary.totalTravelExpenses());
        verify(transactionRepository).findMonthlyTransactions(accountNumber, startDate, endDate, TransactionStatus.COMPLETED);
        verify(paymentRepository).findMonthlyPayments(accountNumber, startDate, endDate, PaymentStatus.COMPLETED);
    }
}
