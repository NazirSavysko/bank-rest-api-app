package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.CartItemDTO;
import bank.rest.app.bankrestapp.dto.CommunalPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.ElectronicsPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.MobilePaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.TaxPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.TrainPaymentRequestDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.ElectronicsPayment;
import bank.rest.app.bankrestapp.entity.AuthUSer;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.IbanPayment;
import bank.rest.app.bankrestapp.entity.InternetPayment;
import bank.rest.app.bankrestapp.entity.MobilePayment;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.TaxPayment;
import bank.rest.app.bankrestapp.entity.TrainPayment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.UtilityPayment;
import bank.rest.app.bankrestapp.entity.enums.AccountType;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_OWNERSHIP_MISMATCH;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_FOP_ACCOUNT_EDRPOU_REQUIRED;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_INSUFFICIENT_FUNDS;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_UNSUPPORTED_ACCOUNT_CURRENCY_FOR_IBAN_PAYMENT;
import static bank.rest.app.bankrestapp.entity.enums.PaymentStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    private static final String VALID_UA_IBAN = "UA12345678901234567890123456789012";

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

//    @Test
//    void processIbanPayment_Successful() {
//        final Account senderAccount = createAccount(10, Currency.UAH, BigDecimal.valueOf(500), "user@example.com", "UA_SENDER");
//        when(accountRepository.findByIdForUpdate(10)).thenReturn(Optional.of(senderAccount));
//        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
//                10L,
//                BigDecimal.valueOf(100),
//                "ТОВ Тест",
//                VALID_UA_IBAN,
//                "1234567890",
//                "Оплата послуг"
//        );
//
//        final Payment result = paymentService.processIbanPayment(request, "user@example.com");
//
//        assertInstanceOf(IbanPayment.class, result);
//        final IbanPayment ibanPayment = (IbanPayment) result;
//        assertEquals(BigDecimal.valueOf(400), senderAccount.getBalance());
//        assertEquals(COMPLETED, ibanPayment.getStatus());
//        assertEquals("UAH", ibanPayment.getCurrencyCode());
//        assertEquals("ТОВ Тест", ibanPayment.getBeneficiaryName());
//        assertEquals(VALID_UA_IBAN, ibanPayment.getBeneficiaryAcc());
//        assertEquals("1234567890", ibanPayment.getTaxNumber());
//        assertEquals("Оплата послуг", ibanPayment.getPurpose());
//        assertNotNull(ibanPayment.getPaymentDate());
//        assertNotNull(ibanPayment.getTransaction());
//        assertEquals(TransactionType.IBAN_PAYMENT, ibanPayment.getTransaction().getTransactionType());
//        assertEquals("Переказ за IBAN: " + VALID_UA_IBAN + ". До зарахування: 100.00 UAH", ibanPayment.getTransaction().getDescription());
//        assertNull(ibanPayment.getTransaction().getToAccount());
//
//        verify(accountRepository).save(senderAccount);
//        verify(accountRepository).findByIdForUpdate(10);
//        verify(accountRepository, never()).findByAccountNumber(anyString());
//        verify(transactionRepository).save(any(Transaction.class));
//        verify(paymentRepository).save(any(IbanPayment.class));
//    }

//    @Test
//    void processInternetPayment_Successful() {
//        final Account account = createAccount(11, Currency.UAH, BigDecimal.valueOf(300), "user@example.com", "UA_INTERNET_1");
//        when(accountRepository.findByIdForUpdate(11)).thenReturn(Optional.of(account));
//        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        final InternetPaymentRequestDTO request = new InternetPaymentRequestDTO(
//                11L,
//                BigDecimal.valueOf(50),
//                "Lanet",
//                "ACC-001"
//        );
//
//        final Payment result = paymentService.processInternetPayment(request, "user@example.com");
//
//        assertInstanceOf(InternetPayment.class, result);
//        final InternetPayment internetPayment = (InternetPayment) result;
//        assertEquals(BigDecimal.valueOf(250), account.getBalance());
//        assertEquals(COMPLETED, internetPayment.getStatus());
//        assertEquals("Lanet", internetPayment.getBeneficiaryName());
//        assertEquals("ACC-001", internetPayment.getBeneficiaryAcc());
//        assertEquals("Оплата послуг інтернет, провайдер: Lanet, дог. ACC-001", internetPayment.getPurpose());
//        assertNotNull(internetPayment.getTransaction());
//        assertEquals(TransactionType.INTERNET_PAYMENT, internetPayment.getTransaction().getTransactionType());
//        assertEquals("Оплата інтернету (провайдер: Lanet)", internetPayment.getTransaction().getDescription());
//        assertNull(internetPayment.getTransaction().getToAccount());
//
//        verify(accountRepository).save(account);
//        verify(accountRepository).findByIdForUpdate(11);
//        verify(transactionRepository).save(any(Transaction.class));
//        verify(paymentRepository).save(any(InternetPayment.class));
//    }

//    @Test
//    void processIbanPayment_UsdAccount_ShouldDeductOriginalAmount_AndSetUahAmountInDescription() {
//        final Account senderAccount = createAccount(12, Currency.USD, BigDecimal.valueOf(500), "user@example.com", "UA_SENDER");
//        when(accountRepository.findByIdForUpdate(12)).thenReturn(Optional.of(senderAccount));
//        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(currencyLoader.getRate("USD"))
//                .thenReturn(Optional.of(new CurrencyLoader.CurrencyRate("USD", 40.0)));
//
//        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
//                12L,
//                BigDecimal.valueOf(400),
//                "Name",
//                VALID_UA_IBAN,
//                "123",
//                "Purpose"
//        );
//
//        final Payment result = paymentService.processIbanPayment(request, "user@example.com");
//
//        assertInstanceOf(IbanPayment.class, result);
//        final IbanPayment ibanPayment = (IbanPayment) result;
//        assertEquals(BigDecimal.valueOf(100), senderAccount.getBalance());
//        assertEquals(BigDecimal.valueOf(400), result.getAmount());
//        assertEquals("USD", result.getCurrencyCode());
//        assertEquals("Переказ за IBAN: " + VALID_UA_IBAN + ". До зарахування: 16000.00 UAH", ibanPayment.getTransaction().getDescription());
//        assertNull(ibanPayment.getTransaction().getToAccount());
//        verify(currencyLoader).getRate("USD");
//        verify(accountRepository).save(senderAccount);
//        verify(accountRepository, never()).findByAccountNumber(anyString());
//        verify(transactionRepository).save(any(Transaction.class));
//        verify(paymentRepository).save(any(IbanPayment.class));
//    }

//    @Test
//    void processIbanPayment_EurAccount_ShouldSetUahAmountInDescription() {
//        final Account senderAccount = createAccount(19, Currency.EUR, BigDecimal.valueOf(100), "user@example.com", "UA_SENDER");
//        when(accountRepository.findByIdForUpdate(19)).thenReturn(Optional.of(senderAccount));
//        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(currencyLoader.getRate("EUR"))
//                .thenReturn(Optional.of(new CurrencyLoader.CurrencyRate("EUR", 42.0)));
//
//        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
//                19L,
//                BigDecimal.valueOf(10),
//                "Name",
//                VALID_UA_IBAN,
//                "123",
//                "Purpose"
//        );
//
//        final Payment result = paymentService.processIbanPayment(request, "user@example.com");
//
//        assertInstanceOf(IbanPayment.class, result);
//        final IbanPayment ibanPayment = (IbanPayment) result;
//        assertEquals(BigDecimal.valueOf(90), senderAccount.getBalance());
//        assertEquals(BigDecimal.valueOf(10), result.getAmount());
//        assertEquals("EUR", result.getCurrencyCode());
//        assertEquals("Переказ за IBAN: " + VALID_UA_IBAN + ". До зарахування: 420.00 UAH", ibanPayment.getTransaction().getDescription());
//        assertNull(ibanPayment.getTransaction().getToAccount());
//        verify(currencyLoader).getRate("EUR");
//        verify(transactionRepository).save(any(Transaction.class));
//    }

    @Test
    void processInternetPayment_InsufficientFunds_ShouldThrow() {
        final Account account = createAccount(13, Currency.UAH, BigDecimal.valueOf(20), "user@example.com", "UA_INTERNET_2");
        when(accountRepository.findByIdForUpdate(13)).thenReturn(Optional.of(account));

        final InternetPaymentRequestDTO request = new InternetPaymentRequestDTO(
                13L,
                BigDecimal.valueOf(50),
                "Lanet",
                "A-01"
        );

        final InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> paymentService.processInternetPayment(request, "user@example.com"));
        assertEquals(ERRORS_INSUFFICIENT_FUNDS, exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processMobilePayment_Successful() {
        final Account account = createAccount(31, Currency.UAH, BigDecimal.valueOf(300), "user@example.com", "UA_MOBILE_1");
        when(accountRepository.findByIdForUpdate(31)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final MobilePaymentRequestDTO request = new MobilePaymentRequestDTO(
                31L,
                BigDecimal.valueOf(50),
                "+380991112233"
        );

        final Payment result = paymentService.processMobilePayment(request, "user@example.com");

        assertInstanceOf(MobilePayment.class, result);
        final MobilePayment mobilePayment = (MobilePayment) result;
        assertEquals(BigDecimal.valueOf(250), account.getBalance());
        assertEquals(COMPLETED, mobilePayment.getStatus());
        assertEquals("+380991112233", mobilePayment.getBeneficiaryAcc());
        assertEquals("Mobile top-up: +380991112233", mobilePayment.getPurpose());
        assertNotNull(mobilePayment.getTransaction());
        assertEquals(TransactionType.PAYMENT, mobilePayment.getTransaction().getTransactionType());
        assertEquals("Поповнення мобільного: +380991112233", mobilePayment.getTransaction().getDescription());

        verify(accountRepository).save(account);
        verify(accountRepository).findByIdForUpdate(31);
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentRepository).save(any(MobilePayment.class));
    }

    @Test
    void processMobilePayment_NonUahAccount_ShouldThrow() {
        final Account account = createAccount(32, Currency.USD, BigDecimal.valueOf(300), "user@example.com", "UA_MOBILE_2");
        when(accountRepository.findByIdForUpdate(32)).thenReturn(Optional.of(account));

        final MobilePaymentRequestDTO request = new MobilePaymentRequestDTO(
                32L,
                BigDecimal.valueOf(50),
                "+380991112233"
        );

        final InvalidAccountCurrencyException exception = assertThrows(InvalidAccountCurrencyException.class,
                () -> paymentService.processMobilePayment(request, "user@example.com"));
        assertEquals("Пополнение мобильного возможно только с гривневого счета", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processIbanPayment_AccountOwnershipMismatch_ShouldThrow() {
        final Account account = createAccount(14, Currency.UAH, BigDecimal.valueOf(200), "owner@example.com", "UA_SENDER");
        when(accountRepository.findByIdForUpdate(14)).thenReturn(Optional.of(account));

        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                14L,
                BigDecimal.TEN,
                "Name",
                VALID_UA_IBAN,
                "123",
                "Purpose"
        );

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentService.processIbanPayment(request, "other@example.com"));
        assertEquals(ERRORS_ACCOUNT_OWNERSHIP_MISMATCH, exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

//    @Test
//    void processTaxPayment_Successful() {
//        final Account account = createAccount(41, Currency.UAH, BigDecimal.valueOf(500), "user@example.com", "UA_TAX_1");
//        when(accountRepository.findByIdForUpdate(41)).thenReturn(Optional.of(account));
//        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        final TaxPaymentRequestDTO request = new TaxPaymentRequestDTO();
//        request.setAccountId(41L);
//        request.setAmount(BigDecimal.valueOf(120));
//        request.setTaxType("Єдиний податок (5% від доходу)");
//        request.setPeriod("I квартал 2026 року");
//        request.setReceiverName("Держказначейство");
//
//        final Payment result = paymentService.processTaxPayment(request, "user@example.com");
//
//        assertInstanceOf(TaxPayment.class, result);
//        final TaxPayment taxPayment = (TaxPayment) result;
//        assertEquals(BigDecimal.valueOf(380), account.getBalance());
//        assertEquals(COMPLETED, taxPayment.getStatus());
//        assertEquals("Держказначейство", taxPayment.getBeneficiaryName());
//        assertEquals("Податок: Єдиний податок (5% від доходу), Період: I квартал 2026 року", taxPayment.getPurpose());
//        assertEquals("UAH", taxPayment.getCurrencyCode());
//        assertNotNull(taxPayment.getTransaction());
//        assertEquals(TransactionType.PAYMENT, taxPayment.getTransaction().getTransactionType());
//        assertEquals("Оплата податків: Єдиний податок (5% від доходу), I квартал 2026 року",
//                taxPayment.getTransaction().getDescription());
//        assertNull(taxPayment.getTransaction().getToAccount());
//
//        verify(accountRepository).save(account);
//        verify(accountRepository).findByIdForUpdate(41);
//        verify(transactionRepository).save(any(Transaction.class));
//        verify(paymentRepository).save(any(TaxPayment.class));
//    }
//
//    @Test
//    void processTaxPayment_InsufficientFunds_ShouldThrow() {
//        final Account account = createAccount(42, Currency.UAH, BigDecimal.valueOf(20), "user@example.com", "UA_TAX_2");
//        when(accountRepository.findByIdForUpdate(42)).thenReturn(Optional.of(account));
//
//        final TaxPaymentRequestDTO request = new TaxPaymentRequestDTO();
//        request.setAccountId(42L);
//        request.setAmount(BigDecimal.valueOf(50));
//        request.setTaxType("Єдиний податок");
//        request.setPeriod("I квартал 2026 року");
//        request.setReceiverName("Держказначейство");
//
//        final InsufficientFundsException exception = assertThrows(
//                InsufficientFundsException.class,
//                () -> paymentService.processTaxPayment(request, "user@example.com")
//        );
//        assertEquals(ERRORS_INSUFFICIENT_FUNDS, exception.getMessage());
//
//        verify(accountRepository, never()).save(any(Account.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }

//    @Test
//    void processElectronicsPayment_Successful() {
//        final Account account = createAccount(51, Currency.UAH, BigDecimal.valueOf(150000), "user@example.com", "UA_ELEC_1");
//        when(accountRepository.findByIdForUpdate(51)).thenReturn(Optional.of(account));
//        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        final ElectronicsPaymentRequestDTO request = new ElectronicsPaymentRequestDTO();
//        request.setAccountId(51L);
//        request.setTotalAmount(BigDecimal.valueOf(120000));
//        request.setItems(List.of(
//                new CartItemDTO("iPhone 15", 1, BigDecimal.valueOf(100000)),
//                new CartItemDTO("AirPods", 2, BigDecimal.valueOf(10000))
//        ));
//
//        final Payment result = paymentService.processElectronicsPayment("user@example.com", request);
//
//        assertInstanceOf(ElectronicsPayment.class, result);
//        final ElectronicsPayment electronicsPayment = (ElectronicsPayment) result;
//        assertEquals(BigDecimal.valueOf(30000), account.getBalance());
//        assertEquals(COMPLETED, electronicsPayment.getStatus());
//        assertEquals("Marketplace", electronicsPayment.getBeneficiaryName());
//        assertEquals("UAH", electronicsPayment.getCurrencyCode());
//        assertEquals("Купівля електроніки: iPhone 15 (1 шт), AirPods (2 шт)", electronicsPayment.getPurpose());
//        assertNotNull(electronicsPayment.getTransaction());
//        assertEquals(TransactionType.PAYMENT, electronicsPayment.getTransaction().getTransactionType());
//        assertEquals("Купівля електроніки: iPhone 15 (1 шт), AirPods (2 шт)",
//                electronicsPayment.getTransaction().getDescription());
//        assertNull(electronicsPayment.getTransaction().getToAccount());
//
//        verify(accountRepository).save(account);
//        verify(accountRepository).findByIdForUpdate(51);
//        verify(transactionRepository).save(any(Transaction.class));
//        verify(paymentRepository).save(any(ElectronicsPayment.class));
//    }
//
//    @Test
//    void processElectronicsPayment_NonUahAccount_ShouldThrow() {
//        final Account account = createAccount(52, Currency.USD, BigDecimal.valueOf(5000), "user@example.com", "UA_ELEC_2");
//        when(accountRepository.findByIdForUpdate(52)).thenReturn(Optional.of(account));
//
//        final ElectronicsPaymentRequestDTO request = new ElectronicsPaymentRequestDTO();
//        request.setAccountId(52L);
//        request.setTotalAmount(BigDecimal.valueOf(1000));
//        request.setItems(List.of(
//                new CartItemDTO("Powerbank", 1, BigDecimal.valueOf(1000))
//        ));
//
//        final InvalidAccountCurrencyException exception = assertThrows(
//                InvalidAccountCurrencyException.class,
//                () -> paymentService.processElectronicsPayment("user@example.com", request)
//        );
//        assertEquals("Оплата за електроніку можлива лише з гривневого рахунку", exception.getMessage());
//
//        verify(accountRepository, never()).save(any(Account.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }
//
//    @Test
//    void processElectronicsPayment_InvalidCartTotal_ShouldThrow() {
//        final Account account = createAccount(53, Currency.UAH, BigDecimal.valueOf(5000), "user@example.com", "UA_ELEC_3");
//        when(accountRepository.findByIdForUpdate(53)).thenReturn(Optional.of(account));
//
//        final ElectronicsPaymentRequestDTO request = new ElectronicsPaymentRequestDTO();
//        request.setAccountId(53L);
//        request.setTotalAmount(BigDecimal.valueOf(1200));
//        request.setItems(List.of(
//                new CartItemDTO("Router", 1, BigDecimal.valueOf(1000))
//        ));
//
//        final IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> paymentService.processElectronicsPayment("user@example.com", request)
//        );
//        assertEquals("Невірна сума кошика", exception.getMessage());
//
//        verify(accountRepository, never()).save(any(Account.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }
//
//    @Test
//    void processElectronicsPayment_InsufficientFunds_ShouldThrow() {
//        final Account account = createAccount(54, Currency.UAH, BigDecimal.valueOf(1000), "user@example.com", "UA_ELEC_4");
//        when(accountRepository.findByIdForUpdate(54)).thenReturn(Optional.of(account));
//
//        final ElectronicsPaymentRequestDTO request = new ElectronicsPaymentRequestDTO();
//        request.setAccountId(54L);
//        request.setTotalAmount(BigDecimal.valueOf(2000));
//        request.setItems(List.of(
//                new CartItemDTO("SSD", 1, BigDecimal.valueOf(2000))
//        ));
//
//        final InsufficientFundsException exception = assertThrows(
//                InsufficientFundsException.class,
//                () -> paymentService.processElectronicsPayment("user@example.com", request)
//        );
//        assertEquals(ERRORS_INSUFFICIENT_FUNDS, exception.getMessage());
//
//        verify(accountRepository, never()).save(any(Account.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }

    @Test
    void processTrainPayment_Successful() {
        final Account account = createAccount(61, Currency.UAH, BigDecimal.valueOf(2000), "user@example.com", "UA_TRAIN_1");
        when(accountRepository.findByIdForUpdate(61)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final TrainPaymentRequestDTO request = new TrainPaymentRequestDTO();
        request.setAccountId(61L);
        request.setAmount(BigDecimal.valueOf(750));
        request.setFromCity("Київ");
        request.setToCity("Одеса");
        request.setDepartureDate(LocalDate.of(2026, 4, 1));
        request.setTicketType("Купе");

        final Payment result = paymentService.processTrainPayment("user@example.com", request);

        assertInstanceOf(TrainPayment.class, result);
        final TrainPayment trainPayment = (TrainPayment) result;
        assertEquals(BigDecimal.valueOf(1250), account.getBalance());
        assertEquals(COMPLETED, trainPayment.getStatus());
        assertEquals("UAH", trainPayment.getCurrencyCode());
        assertEquals("Укрзалізниця", trainPayment.getBeneficiaryName());
        assertEquals("Квиток на потяг: Київ - Одеса, Дата: 2026-04-01 (Купе)", trainPayment.getPurpose());
        assertNotNull(trainPayment.getTransaction());
        assertEquals(TransactionType.PAYMENT, trainPayment.getTransaction().getTransactionType());
        assertEquals("Квиток на потяг: Київ - Одеса, Дата: 2026-04-01 (Купе)",
                trainPayment.getTransaction().getDescription());
        assertNull(trainPayment.getTransaction().getToAccount());

        verify(accountRepository).save(account);
        verify(accountRepository).findByIdForUpdate(61);
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentRepository).save(any(TrainPayment.class));
    }

    @Test
    void processTrainPayment_NonUahAccount_ShouldThrow() {
        final Account account = createAccount(62, Currency.USD, BigDecimal.valueOf(2000), "user@example.com", "UA_TRAIN_2");
        when(accountRepository.findByIdForUpdate(62)).thenReturn(Optional.of(account));

        final TrainPaymentRequestDTO request = new TrainPaymentRequestDTO();
        request.setAccountId(62L);
        request.setAmount(BigDecimal.valueOf(500));
        request.setFromCity("Київ");
        request.setToCity("Харків");
        request.setDepartureDate(LocalDate.of(2026, 4, 2));
        request.setTicketType("Плацкарт");

        final InvalidAccountCurrencyException exception = assertThrows(
                InvalidAccountCurrencyException.class,
                () -> paymentService.processTrainPayment("user@example.com", request)
        );
        assertEquals("Платежі дозволені лише з рахунків у гривні", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processTrainPayment_InsufficientFunds_ShouldThrow() {
        final Account account = createAccount(63, Currency.UAH, BigDecimal.valueOf(100), "user@example.com", "UA_TRAIN_3");
        when(accountRepository.findByIdForUpdate(63)).thenReturn(Optional.of(account));

        final TrainPaymentRequestDTO request = new TrainPaymentRequestDTO();
        request.setAccountId(63L);
        request.setAmount(BigDecimal.valueOf(500));
        request.setFromCity("Київ");
        request.setToCity("Дніпро");
        request.setDepartureDate(LocalDate.of(2026, 4, 3));
        request.setTicketType("Інтерсіті");

        final InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> paymentService.processTrainPayment("user@example.com", request)
        );
        assertEquals(ERRORS_INSUFFICIENT_FUNDS, exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processTrainPayment_PastDepartureDate_ShouldThrow() {
        final TrainPaymentRequestDTO request = new TrainPaymentRequestDTO();
        request.setAccountId(64L);
        request.setAmount(BigDecimal.valueOf(500));
        request.setFromCity("Київ");
        request.setToCity("Луцьк");
        request.setDepartureDate(LocalDate.now().minusDays(1));
        request.setTicketType("Купе");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processTrainPayment("user@example.com", request)
        );
        assertEquals("Дата поїздки має бути сьогодні або в майбутньому", exception.getMessage());

        verifyNoInteractions(accountRepository, transactionRepository, paymentRepository);
    }

    @Test
    void processCommunalPayment_Successful() {
        final Account account = createAccount(71, Currency.UAH, BigDecimal.valueOf(3000), "user@example.com", "UA_COMMUNAL_1");
        when(accountRepository.findByIdForUpdate(71)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final CommunalPaymentRequestDTO request = new CommunalPaymentRequestDTO();
        request.setAccountId(71L);
        request.setAmount(BigDecimal.valueOf(980));
        request.setUtilityProvider("KyivEnergo");
        request.setPersonalAccount("1234567890");

        final Payment result = paymentService.processCommunalPayment("user@example.com", request);

        assertInstanceOf(UtilityPayment.class, result);
        final UtilityPayment utilityPayment = (UtilityPayment) result;
        assertEquals(BigDecimal.valueOf(2020), account.getBalance());
        assertEquals(COMPLETED, utilityPayment.getStatus());
        assertEquals("UAH", utilityPayment.getCurrencyCode());
        assertEquals("KyivEnergo", utilityPayment.getProviderName());
        assertEquals("1234567890", utilityPayment.getUtilityAccountNumber());
        assertEquals("KyivEnergo", utilityPayment.getBeneficiaryName());
        assertEquals("Оплата комунальних послуг: KyivEnergo (Ор: 1234567890)", utilityPayment.getPurpose());
        assertNotNull(utilityPayment.getTransaction());
        assertEquals(TransactionType.UTILITY_PAYMENT, utilityPayment.getTransaction().getTransactionType());
        assertEquals("Оплата комунальних послуг: KyivEnergo (Ор: 1234567890)", utilityPayment.getTransaction().getDescription());
        assertEquals(BigDecimal.valueOf(980), utilityPayment.getTransaction().getAmount());
        assertNull(utilityPayment.getTransaction().getToAccount());

        verify(accountRepository).save(account);
        verify(accountRepository).findByIdForUpdate(71);
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentRepository).save(any(UtilityPayment.class));
        verifyNoInteractions(currencyLoader);
    }

    @Test
    void processCommunalPayment_NonUahAccount_ShouldConvertDeduction() {
        final Account account = createAccount(72, Currency.USD, BigDecimal.valueOf(100), "user@example.com", "UA_COMMUNAL_2");
        when(accountRepository.findByIdForUpdate(72)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(currencyLoader.convert(BigDecimal.valueOf(800), "UAH", "USD")).thenReturn(BigDecimal.valueOf(20));

        final CommunalPaymentRequestDTO request = new CommunalPaymentRequestDTO();
        request.setAccountId(72L);
        request.setAmount(BigDecimal.valueOf(800));
        request.setUtilityProvider("Water");
        request.setPersonalAccount("PA-22");

        final Payment result = paymentService.processCommunalPayment("user@example.com", request);

        assertInstanceOf(UtilityPayment.class, result);
        assertEquals(BigDecimal.valueOf(80), account.getBalance());
        assertEquals(BigDecimal.valueOf(800), result.getAmount());
        assertEquals("UAH", result.getCurrencyCode());
        assertNotNull(result.getTransaction());
        assertEquals(BigDecimal.valueOf(20), result.getTransaction().getAmount());
        assertEquals(Currency.USD, result.getTransaction().getCurrencyCode());

        verify(currencyLoader).convert(BigDecimal.valueOf(800), "UAH", "USD");
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
        verify(paymentRepository).save(any(UtilityPayment.class));
    }

    @Test
    void processCommunalPayment_InsufficientFunds_ShouldThrow() {
        final Account account = createAccount(73, Currency.EUR, BigDecimal.valueOf(5), "user@example.com", "UA_COMMUNAL_3");
        when(accountRepository.findByIdForUpdate(73)).thenReturn(Optional.of(account));
        when(currencyLoader.convert(BigDecimal.valueOf(1000), "UAH", "EUR")).thenReturn(BigDecimal.valueOf(25));

        final CommunalPaymentRequestDTO request = new CommunalPaymentRequestDTO();
        request.setAccountId(73L);
        request.setAmount(BigDecimal.valueOf(1000));
        request.setUtilityProvider("Gas");
        request.setPersonalAccount("ACC-73");

        final InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> paymentService.processCommunalPayment("user@example.com", request)
        );
        assertEquals(ERRORS_INSUFFICIENT_FUNDS, exception.getMessage());

        verify(currencyLoader).convert(BigDecimal.valueOf(1000), "UAH", "EUR");
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
                "PL123456789012345678901234567890",
                "123",
                "Purpose"
        );

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processIbanPayment(request, "user@example.com"));

        verifyNoInteractions(accountRepository, paymentRepository, transactionRepository);
    }

    @Test
    void processIbanPayment_InvalidIbanLength_ShouldThrow() {
        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                15L,
                BigDecimal.TEN,
                "Name",
                "UA123456789012345678901234567",
                "123",
                "Purpose"
        );

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processIbanPayment(request, "user@example.com"));

        verifyNoInteractions(accountRepository, paymentRepository, transactionRepository);
    }

//    @Test
//    void processIbanPayment_UnsupportedCurrency_ShouldThrow() {
//        final Account senderAccount = createAccount(16, null, BigDecimal.valueOf(200), "user@example.com", "UA_SENDER");
//        when(accountRepository.findByIdForUpdate(16)).thenReturn(Optional.of(senderAccount));
//
//        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
//                16L,
//                BigDecimal.valueOf(100),
//                "Name",
//                VALID_UA_IBAN,
//                "123",
//                "Purpose"
//        );
//
//        final UnsupportedCurrencyException exception = assertThrows(UnsupportedCurrencyException.class,
//                () -> paymentService.processIbanPayment(request, "user@example.com"));
//        assertEquals(ERRORS_UNSUPPORTED_ACCOUNT_CURRENCY_FOR_IBAN_PAYMENT, exception.getMessage());
//
//        verify(accountRepository, never()).save(any(Account.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(paymentRepository, never()).save(any(Payment.class));
//        verifyNoInteractions(currencyLoader);
//    }

//    @Test
//    void processIbanPayment_FopWithoutEdrpou_ShouldThrow() {
//        final Account senderAccount = createAccount(17, Currency.UAH, BigDecimal.valueOf(200), "user@example.com", "UA_SENDER");
//        senderAccount.setAccountType(AccountType.FOP);
//        senderAccount.setEdrpou(null);
//        when(accountRepository.findByIdForUpdate(17)).thenReturn(Optional.of(senderAccount));
//
//        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
//                17L,
//                BigDecimal.valueOf(100),
//                "Name",
//                VALID_UA_IBAN,
//                "123",
//                "Purpose"
//        );
//
//        final IllegalStateException exception = assertThrows(IllegalStateException.class,
//                () -> paymentService.processIbanPayment(request, "user@example.com"));
//        assertEquals(ERRORS_FOP_ACCOUNT_EDRPOU_REQUIRED, exception.getMessage());
//
//        verify(accountRepository, never()).save(any(Account.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }

//    @Test
//    void processIbanPayment_InsufficientFunds_ShouldThrow() {
//        final Account senderAccount = createAccount(21, Currency.UAH, BigDecimal.valueOf(20), "user@example.com", "UA_SENDER");
//        when(accountRepository.findByIdForUpdate(21)).thenReturn(Optional.of(senderAccount));
//
//        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
//                21L,
//                BigDecimal.valueOf(100),
//                "Name",
//                VALID_UA_IBAN,
//                "123",
//                "Purpose"
//        );
//
//        final InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
//                () -> paymentService.processIbanPayment(request, "user@example.com"));
//        assertEquals(ERRORS_INSUFFICIENT_FUNDS, exception.getMessage());
//
//        verify(accountRepository, never()).save(any(Account.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//        verify(paymentRepository, never()).save(any(Payment.class));
//    }
//
//    @Test
//    void processIbanPayment_ExternalRecipientIban_ShouldNotLookupRecipientAccount() {
//        final Account senderAccount = createAccount(18, Currency.USD, BigDecimal.valueOf(200), "user@example.com", "UA_SENDER");
//        when(accountRepository.findByIdForUpdate(18)).thenReturn(Optional.of(senderAccount));
//        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(currencyLoader.getRate("USD"))
//                .thenReturn(Optional.of(new CurrencyLoader.CurrencyRate("USD", 40.0)));
//
//        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
//                18L,
//                BigDecimal.valueOf(100),
//                "Name",
//                VALID_UA_IBAN,
//                "123",
//                "Purpose"
//        );
//
//        final Payment payment = paymentService.processIbanPayment(request, "user@example.com");
//
//        assertInstanceOf(IbanPayment.class, payment);
//        verify(accountRepository).save(senderAccount);
//        verify(accountRepository, never()).findByAccountNumber(anyString());
//        verify(transactionRepository).save(any(Transaction.class));
//        verify(paymentRepository).save(any(IbanPayment.class));
//    }

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
        account.setAccountType(AccountType.CURRENT);
        account.setCurrencyCode(currency);
        account.setBalance(balance);
        account.setCustomer(customer);

        return account;
    }
}
