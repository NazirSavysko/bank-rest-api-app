package bank.rest.app.bankrestapp.service.impl;

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
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.PaymentRepository;
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

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void processIbanPayment_Successful() {
        final Account account = createAccount(10, Currency.UAH, BigDecimal.valueOf(500), "user@example.com");
        when(accountRepository.findById(10)).thenReturn(Optional.of(account));
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
        assertEquals(BigDecimal.valueOf(400), account.getBalance());
        assertEquals(COMPLETED, ibanPayment.getStatus());
        assertEquals("UAH", ibanPayment.getCurrencyCode());
        assertEquals("ТОВ Тест", ibanPayment.getBeneficiaryName());
        assertEquals("UA123456789012345678901234567", ibanPayment.getBeneficiaryAcc());
        assertEquals("1234567890", ibanPayment.getTaxNumber());
        assertEquals("Оплата послуг", ibanPayment.getPurpose());
        assertNotNull(ibanPayment.getPaymentDate());

        verify(accountRepository).save(account);
        verify(paymentRepository).save(any(IbanPayment.class));
    }

    @Test
    void processInternetPayment_Successful() {
        final Account account = createAccount(11, Currency.UAH, BigDecimal.valueOf(300), "user@example.com");
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
    }

    @Test
    void processIbanPayment_NonUahAccount_ShouldThrow() {
        final Account account = createAccount(12, Currency.USD, BigDecimal.valueOf(500), "user@example.com");
        when(accountRepository.findById(12)).thenReturn(Optional.of(account));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                12L,
                BigDecimal.valueOf(100),
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        assertThrows(InvalidAccountCurrencyException.class,
                () -> paymentService.processIbanPayment(request, "user@example.com"));

        verify(accountRepository, never()).save(any(Account.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processInternetPayment_InsufficientFunds_ShouldThrow() {
        final Account account = createAccount(13, Currency.UAH, BigDecimal.valueOf(20), "user@example.com");
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
    }

    @Test
    void processIbanPayment_AccountOwnershipMismatch_ShouldThrow() {
        final Account account = createAccount(14, Currency.UAH, BigDecimal.valueOf(200), "owner@example.com");
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

        verifyNoInteractions(accountRepository, paymentRepository);
    }

    private Account createAccount(final Integer id,
                                  final Currency currency,
                                  final BigDecimal balance,
                                  final String email) {
        final AuthUSer authUser = new AuthUSer();
        authUser.setEmail(email);

        final Customer customer = new Customer();
        customer.setAuthUser(authUser);

        final Account account = new Account();
        account.setAccountId(id);
        account.setCurrencyCode(currency);
        account.setBalance(balance);
        account.setCustomer(customer);

        return account;
    }
}
