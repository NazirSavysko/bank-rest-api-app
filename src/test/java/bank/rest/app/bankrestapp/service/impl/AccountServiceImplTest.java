package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static bank.rest.app.bankrestapp.constants.AccountDefaults.MAXIMUM_NUMBER_OF_ACCOUNTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

        // Act
        Account account = accountService.generateAccountByCurrencyCode(Currency.USD);

        // Assert
        assertNotNull(account);
        assertEquals(Currency.USD, account.getCurrencyCode());
        assertTrue(account.getAccountNumber().startsWith("US"));
        verify(accountRepository).existsByAccountNumber(anyString());
    }

    @Test
    void createAccount_Success() {
        // Arrange
        String email = "test@example.com";
        String accountType = "EUR";
        Customer customer = new Customer();
        customer.setAccounts(new ArrayList<>());
        Card card = new Card();

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(cardService.generateCard()).thenReturn(card);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Account created = accountService.createAccount(accountType, email);

        // Assert
        assertNotNull(created);
        assertEquals(Currency.EUR, created.getCurrencyCode());
        assertEquals(customer, created.getCustomer());
        assertNotNull(created.getCard());
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

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("USD", email));
    }

    @Test
    void createAccount_DuplicateCurrency() {
        // Arrange
        String email = "test@example.com";
        Customer customer = new Customer();
        Account existing = new Account();
        existing.setCurrencyCode(Currency.USD);
        customer.setAccounts(List.of(existing));

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(cardService.generateCard()).thenReturn(new Card());
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("USD", email));
    }

    @Test
    void getAccountByNumber_Success() {
        // Arrange
        String accNum = "UA123";
        Account account = new Account();
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
        assertThrows(NoSuchElementException.class, () -> accountService.getAccountByNumber(accNum));
    }
}