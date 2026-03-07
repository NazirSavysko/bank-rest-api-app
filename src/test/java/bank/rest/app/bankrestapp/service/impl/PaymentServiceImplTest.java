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
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.exception.InvalidAccountCurrencyException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import bank.rest.app.bankrestapp.exception.RecipientNotFoundException;
import bank.rest.app.bankrestapp.exception.UnsupportedCurrencyException;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.TransactionType;
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
    private TransactionRepository transactionRepository;

    @Mock
    private CurrencyLoader currencyLoader;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void processIbanPayment_Successful() {
        final Account senderAccount = createAccount(10, Currency.UAH, BigDecimal.valueOf(500), "user@example.com", "UA_SENDER");
        final Account recipientAccount = createAccount(20, Currency.UAH, BigDecimal.valueOf(200), "recipient@example.com", "UA123456789012345678901234567");
        when(accountRepository.findById(10)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("UA123456789012345678901234567")).thenReturn(Optional.of(recipientAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
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
        assertEquals(BigDecimal.valueOf(300), recipientAccount.getBalance());
        assertEquals(COMPLETED, ibanPayment.getStatus());
        assertEquals("UAH", ibanPayment.getCurrencyCode());
        assertEquals("ТОВ Тест", ibanPayment.getBeneficiaryName());
        assertEquals("UA123456789012345678901234567", ibanPayment.getBeneficiaryAcc());
        assertEquals("1234567890", ibanPayment.getTaxNumber());
        assertEquals("Оплата послуг", ibanPayment.getPurpose());
        assertNotNull(ibanPayment.getPaymentDate());

        verify(accountRepository).save(senderAccount);
        verify(accountRepository).save(recipientAccount);
        verify(paymentRepository).save(any(IbanPayment.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void processInternetPayment_Successful() {
        final Account account = createAccount(11, Currency.UAH, BigDecimal.valueOf(300), "user@example.com", "UA_INTERNET_1");
        when(accountRepository.findById(11)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
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

        verify(accountRepository).save(account);
        verify(paymentRepository).save(any(InternetPayment.class));
        verify(transactionRepository).save(argThat(t ->
                t.getTransactionType() == TransactionType.INTERNET_PAYMENT
                && t.getToAccount() == null));
    }

    @Test
    void processIbanPayment_UsdAccount_ShouldConvertAndDeductConvertedAmount() {
        final Account senderAccount = createAccount(12, Currency.USD, BigDecimal.valueOf(500), "user@example.com", "UA_SENDER");
        final Account recipientAccount = createAccount(22, Currency.UAH, BigDecimal.valueOf(100), "recipient@example.com", "UA123456789012345678901234567");
        when(accountRepository.findById(12)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("UA123456789012345678901234567")).thenReturn(Optional.of(recipientAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
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
        assertEquals(new BigDecimal("490.00"), senderAccount.getBalance());
        assertEquals(BigDecimal.valueOf(500), recipientAccount.getBalance());
        assertEquals(BigDecimal.valueOf(400), result.getAmount());
        assertEquals("UAH", result.getCurrencyCode());
        verify(currencyLoader).getRate("USD");
        verify(accountRepository).save(senderAccount);
        verify(accountRepository).save(recipientAccount);
        verify(paymentRepository).save(any(IbanPayment.class));
    }

    @Test
    void processIbanPayment_EurAccount_ShouldDivideUahByRateAndRoundHalfUp() {
        final Account senderAccount = createAccount(19, Currency.EUR, BigDecimal.valueOf(100), "user@example.com", "UA_SENDER");
        final Account recipientAccount = createAccount(29, Currency.UAH, BigDecimal.valueOf(50), "recipient@example.com", "UA123456789012345678901234567");
        when(accountRepository.findById(19)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("UA123456789012345678901234567")).thenReturn(Optional.of(recipientAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(currencyLoader.getRate("EUR"))
                .thenReturn(Optional.of(new CurrencyLoader.CurrencyRate("EUR", 42.0)));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                19L,
                BigDecimal.valueOf(1000),
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        final Payment result = paymentService.processIbanPayment(request, "user@example.com");

        assertInstanceOf(IbanPayment.class, result);
        assertEquals(new BigDecimal("76.19"), senderAccount.getBalance());
        assertEquals(BigDecimal.valueOf(1050), recipientAccount.getBalance());
        assertEquals(BigDecimal.valueOf(1000), result.getAmount());
        assertEquals("UAH", result.getCurrencyCode());
        verify(currencyLoader).getRate("EUR");
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
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
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
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
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
        final Account recipientAccount = createAccount(17, Currency.UAH, BigDecimal.valueOf(200), "recipient@example.com", "UA123456789012345678901234567");
        when(accountRepository.findById(16)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("UA123456789012345678901234567")).thenReturn(Optional.of(recipientAccount));

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
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verifyNoInteractions(currencyLoader);
    }

    @Test
    void processIbanPayment_RecipientIbanNotFound_ShouldThrow() {
        final Account senderAccount = createAccount(18, Currency.USD, BigDecimal.valueOf(200), "user@example.com", "UA_SENDER");
        when(accountRepository.findById(18)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("UA123456789012345678901234567")).thenReturn(Optional.empty());

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                18L,
                BigDecimal.valueOf(100),
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        final RecipientNotFoundException exception = assertThrows(RecipientNotFoundException.class,
                () -> paymentService.processIbanPayment(request, "user@example.com"));
        assertEquals("Recipient IBAN not found in the system", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verifyNoInteractions(currencyLoader);
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
