package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.AuthUSer;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.IbanPayment;
import bank.rest.app.bankrestapp.entity.InternetPayment;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.TransactionType;
import bank.rest.app.bankrestapp.exception.InvalidAccountCurrencyException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import bank.rest.app.bankrestapp.exception.UnsupportedCurrencyException;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.PaymentRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static bank.rest.app.bankrestapp.entity.enums.PaymentStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CurrencyLoader currencyLoader;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void processIbanPayment_Successful() {
        final Account senderAccount = createAccount(10, Currency.UAH, BigDecimal.valueOf(500), "user@example.com", "UA_SENDER");
        when(accountRepository.findById(10)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                10L,
                BigDecimal.valueOf(100),
                "ТОВ Тест",
                "UA123456789012345678901234567",
                "1234567890",
                "Оплата послуг"
        );

        final Payment result = paymentService.processIbanPayment(request, "user@example.com");

        assertInstanceOf(IbanPayment.class, result);
        final IbanPayment ibanPayment = (IbanPayment) result;
        assertEquals(BigDecimal.valueOf(400), senderAccount.getBalance());
        assertEquals(COMPLETED, ibanPayment.getStatus());
        assertEquals("UAH", ibanPayment.getCurrencyCode());
        assertEquals("ТОВ Тест", ibanPayment.getBeneficiaryName());
        assertEquals("UA123456789012345678901234567", ibanPayment.getBeneficiaryAcc());
        assertEquals("1234567890", ibanPayment.getTaxNumber());
        assertEquals("Оплата послуг", ibanPayment.getPurpose());
        assertNotNull(ibanPayment.getPaymentDate());
        assertNotNull(ibanPayment.getTransaction());
        assertEquals(TransactionType.IBAN_PAYMENT, ibanPayment.getTransaction().getTransactionType());
        assertEquals("Переказ за IBAN: UA123456789012345678901234567. До зарахування: 100.00 UAH", ibanPayment.getTransaction().getDescription());
        assertNull(ibanPayment.getTransaction().getToAccount());

        verify(accountRepository).save(senderAccount);
        verify(accountRepository, never()).findByAccountNumber(anyString());
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentRepository).save(any(IbanPayment.class));
    }

    @Test
    void processInternetPayment_Successful() {
        final Account account = createAccount(11, Currency.UAH, BigDecimal.valueOf(300), "user@example.com", "UA_INTERNET_1");
        when(accountRepository.findById(11)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final InternetPaymentRequestDTO request = new InternetPaymentRequestDTO(
                11L,
                BigDecimal.valueOf(50),
                "Lanet",
                "ACC-001"
        );

        final Payment result = paymentService.processInternetPayment(request, "user@example.com");

        assertInstanceOf(InternetPayment.class, result);
        final InternetPayment internetPayment = (InternetPayment) result;
        assertEquals(BigDecimal.valueOf(250), account.getBalance());
        assertEquals(COMPLETED, internetPayment.getStatus());
        assertEquals("Lanet", internetPayment.getBeneficiaryName());
        assertEquals("ACC-001", internetPayment.getBeneficiaryAcc());
        assertEquals("Оплата послуг інтернет, провайдер: Lanet, дог. ACC-001", internetPayment.getPurpose());
        assertNotNull(internetPayment.getTransaction());
        assertEquals(TransactionType.INTERNET_PAYMENT, internetPayment.getTransaction().getTransactionType());
        assertEquals("Оплата інтернету (провайдер: Lanet)", internetPayment.getTransaction().getDescription());
        assertNull(internetPayment.getTransaction().getToAccount());

        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentRepository).save(any(InternetPayment.class));
    }

    @Test
    void processIbanPayment_UsdAccount_ShouldDeductOriginalAmount_AndSetUahAmountInDescription() {
        final Account senderAccount = createAccount(12, Currency.USD, BigDecimal.valueOf(500), "user@example.com", "UA_SENDER");
        when(accountRepository.findById(12)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(currencyLoader.getRate("USD"))
                .thenReturn(Optional.of(new CurrencyLoader.CurrencyRate("USD", 40.0)));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                12L,
                BigDecimal.valueOf(400),
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        final Payment result = paymentService.processIbanPayment(request, "user@example.com");

        assertInstanceOf(IbanPayment.class, result);
        final IbanPayment ibanPayment = (IbanPayment) result;
        assertEquals(BigDecimal.valueOf(100), senderAccount.getBalance());
        assertEquals(BigDecimal.valueOf(400), result.getAmount());
        assertEquals("USD", result.getCurrencyCode());
        assertEquals("Переказ за IBAN: UA123456789012345678901234567. До зарахування: 16000.00 UAH", ibanPayment.getTransaction().getDescription());
        assertNull(ibanPayment.getTransaction().getToAccount());
        verify(currencyLoader).getRate("USD");
        verify(accountRepository).save(senderAccount);
        verify(accountRepository, never()).findByAccountNumber(anyString());
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentRepository).save(any(IbanPayment.class));
    }

    @Test
    void processIbanPayment_EurAccount_ShouldSetUahAmountInDescription() {
        final Account senderAccount = createAccount(19, Currency.EUR, BigDecimal.valueOf(100), "user@example.com", "UA_SENDER");
        when(accountRepository.findById(19)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(currencyLoader.getRate("EUR"))
                .thenReturn(Optional.of(new CurrencyLoader.CurrencyRate("EUR", 42.0)));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                19L,
                BigDecimal.valueOf(10),
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        final Payment result = paymentService.processIbanPayment(request, "user@example.com");

        assertInstanceOf(IbanPayment.class, result);
        final IbanPayment ibanPayment = (IbanPayment) result;
        assertEquals(BigDecimal.valueOf(90), senderAccount.getBalance());
        assertEquals(BigDecimal.valueOf(10), result.getAmount());
        assertEquals("EUR", result.getCurrencyCode());
        assertEquals("Переказ за IBAN: UA123456789012345678901234567. До зарахування: 420.00 UAH", ibanPayment.getTransaction().getDescription());
        assertNull(ibanPayment.getTransaction().getToAccount());
        verify(currencyLoader).getRate("EUR");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void processInternetPayment_InsufficientFunds_ShouldThrow() {
        final Account account = createAccount(13, Currency.UAH, BigDecimal.valueOf(20), "user@example.com", "UA_INTERNET_2");
        when(accountRepository.findById(13)).thenReturn(Optional.of(account));

        final InternetPaymentRequestDTO request = new InternetPaymentRequestDTO(
                13L,
                BigDecimal.valueOf(50),
                "Lanet",
                "A-01"
        );

        assertThrows(InsufficientFundsException.class,
                () -> paymentService.processInternetPayment(request, "user@example.com"));

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processIbanPayment_AccountOwnershipMismatch_ShouldThrow() {
        final Account account = createAccount(14, Currency.UAH, BigDecimal.valueOf(200), "owner@example.com", "UA_SENDER");
        when(accountRepository.findById(14)).thenReturn(Optional.of(account));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                14L,
                BigDecimal.TEN,
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processIbanPayment(request, "other@example.com"));

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processIbanPayment_InvalidIbanPrefix_ShouldThrow() {
        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                15L,
                BigDecimal.TEN,
                "Name",
                "PL123456789012345678901234567",
                "123",
                "Purpose"
        );

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processIbanPayment(request, "user@example.com"));

        verifyNoInteractions(accountRepository, paymentRepository, transactionRepository);
    }

    @Test
    void processIbanPayment_UnsupportedCurrency_ShouldThrow() {
        final Account senderAccount = createAccount(16, null, BigDecimal.valueOf(200), "user@example.com", "UA_SENDER");
        when(accountRepository.findById(16)).thenReturn(Optional.of(senderAccount));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                16L,
                BigDecimal.valueOf(100),
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        assertThrows(UnsupportedCurrencyException.class,
                () -> paymentService.processIbanPayment(request, "user@example.com"));

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(currencyLoader);
    }

    @Test
    void processIbanPayment_ExternalRecipientIban_ShouldNotLookupRecipientAccount() {
        final Account senderAccount = createAccount(18, Currency.USD, BigDecimal.valueOf(200), "user@example.com", "UA_SENDER");
        when(accountRepository.findById(18)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(currencyLoader.getRate("USD"))
                .thenReturn(Optional.of(new CurrencyLoader.CurrencyRate("USD", 40.0)));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                18L,
                BigDecimal.valueOf(100),
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        final Payment payment = paymentService.processIbanPayment(request, "user@example.com");

        assertInstanceOf(IbanPayment.class, payment);
        verify(accountRepository).save(senderAccount);
        verify(accountRepository, never()).findByAccountNumber(anyString());
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentRepository).save(any(IbanPayment.class));
    }

    private Account createAccount(final Integer id,
                                  final Currency currency,
                                  final BigDecimal balance,
                                  final String email,
                                  final String accountNumber) {
        final AuthUSer authUser = new AuthUSer();
        authUser.setEmail(email);

        final Customer customer = new Customer();
        customer.setAuthUser(authUser);

        final Account account = new Account();
        account.setAccountId(id);
        account.setAccountNumber(accountNumber);
        account.setCurrencyCode(currency);
        account.setBalance(balance);
        account.setCustomer(customer);

        return account;
    }
}
