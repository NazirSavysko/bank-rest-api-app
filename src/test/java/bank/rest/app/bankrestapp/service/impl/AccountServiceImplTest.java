package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.enums.AccountType;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;



import static bank.rest.app.bankrestapp.constants.AccountDefaults.MAXIMUM_NUMBER_OF_ACCOUNTS;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_NOT_FOUND_BY_NUMBER;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_WITH_CURRENCY_ALREADY_EXISTS;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_MAXIMUM_NUMBER_OF_ACCOUNTS_REACHED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void generateAccountByCurrencyCode_Success() {
        // Arrange
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.existsByEdrpou(anyString())).thenReturn(false);

        // Act
        Account account = accountService.generateAccountByCurrencyCode(Currency.USD);

        // Assert
        assertNotNull(account);
        assertEquals(Currency.USD, account.getCurrencyCode());
        assertNotNull(account.getEdrpou());
        assertTrue(account.getEdrpou().matches("\\d{10}"));
        assertTrue(account.getAccountNumber().startsWith("US"));
        verify(accountRepository).existsByAccountNumber(anyString());
        verify(accountRepository).existsByEdrpou(anyString());
    }

    @Test
    void createAccount_Success() {
        // Arrange
        String email = "test@example.com";
        String accountType = "CURRENT";
        String currency = "EUR";
        Customer customer = new Customer();
        customer.setAccounts(new ArrayList<>());
        Card card = new Card();

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(cardService.generateCard()).thenReturn(card);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.existsByEdrpou(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Account created = accountService.createAccount(accountType, currency, email);

        // Assert
        assertNotNull(created);
        assertEquals(AccountType.CURRENT, created.getAccountType());
        assertEquals(Currency.EUR, created.getCurrencyCode());
        assertNotNull(created.getEdrpou());
        assertTrue(created.getEdrpou().matches("\\d{10}"));
        assertEquals(customer, created.getCustomer());
        assertNotNull(created.getCard());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_Fop_ShouldIgnoreRequestedCurrencyAndGenerateEdrpou() {
        final String email = "test@example.com";
        final Customer customer = new Customer();
        final Account currentUahAccount = new Account();
        currentUahAccount.setCurrencyCode(Currency.UAH);
        currentUahAccount.setAccountType(AccountType.CURRENT);
        customer.setAccounts(new ArrayList<>(List.of(currentUahAccount)));
        final Card card = new Card();

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(cardService.generateCard()).thenReturn(card);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.existsByEdrpou(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        final Account created = accountService.createAccount("FOP", "EUR", email);

        assertNotNull(created);
        assertEquals(AccountType.FOP, created.getAccountType());
        assertEquals(Currency.UAH, created.getCurrencyCode());
        assertNotNull(created.getEdrpou());
        assertTrue(created.getEdrpou().matches("\\d{10}"));
        assertEquals(customer, created.getCustomer());
        verify(accountRepository).existsByEdrpou(anyString());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_MaxAccountsReached() {
        // Arrange
        String email = "test@example.com";
        Customer customer = new Customer();
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < MAXIMUM_NUMBER_OF_ACCOUNTS; i++) {
            accounts.add(new Account());
        }
        customer.setAccounts(accounts);

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(cardService.generateCard()).thenReturn(new Card());
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.existsByEdrpou(anyString())).thenReturn(false);

        // Act & Assert
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("CURRENT", "USD", email));
        assertEquals(ERRORS_MAXIMUM_NUMBER_OF_ACCOUNTS_REACHED, exception.getMessage());
    }

    @Test
    void createAccount_DuplicateCurrency() {
        // Arrange
        String email = "test@example.com";
        Customer customer = new Customer();
        Account existing = new Account();
        existing.setCurrencyCode(Currency.USD);
        existing.setAccountType(AccountType.CURRENT);
        customer.setAccounts(List.of(existing));

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(cardService.generateCard()).thenReturn(new Card());
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.existsByEdrpou(anyString())).thenReturn(false);

        // Act & Assert
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("CURRENT", "USD", email));
        assertEquals(ERRORS_ACCOUNT_WITH_CURRENCY_ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    void getAccountByNumber_Success() {
        // Arrange
        String accNum = "UA123";
        Account account = new Account();
        // ensure accountNumber is set so entity equals/hashCode implementations that rely on accountNumber do not NPE
        account.setAccountNumber(accNum);
        account.setAccountId(1);
        account.setAccountNumber(accNum);
        account.setCurrencyCode(Currency.USD);
        account.setCard(new Card());
        account.setCustomer(new Customer());
        account.setBalance(new BigDecimal(100));
        account.setSentTransactions(new ArrayList<>());
        when(accountRepository.findByAccountNumber(accNum)).thenReturn(Optional.of(account));

        // Act
        Account result = accountService.getAccountByNumber(accNum);

        // Assert
        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void getAccountByNumber_NotFound() {
        // Arrange
        String accNum = "UA123";
        when(accountRepository.findByAccountNumber(accNum)).thenReturn(Optional.empty());

        // Act & Assert
        final NoSuchElementException exception =
                assertThrows(NoSuchElementException.class, () -> accountService.getAccountByNumber(accNum));
        assertEquals(ERRORS_ACCOUNT_NOT_FOUND_BY_NUMBER, exception.getMessage());
    }
}
