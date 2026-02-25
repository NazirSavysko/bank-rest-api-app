package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.entity.*;
import bank.rest.app.bankrestapp.entity.enums.AccountStatus;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrencyLoader currencyLoader;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void withdraw_Successful_SameCurrency() {
        // Arrange
        String senderCard = "1111222233334444";
        String recipientCard = "5555666677778888";
        BigDecimal amount = BigDecimal.valueOf(100);
        String description = "Test Transfer";

        Account senderAccount = createAccount(senderCard, Currency.USD, BigDecimal.valueOf(500));
        Account recipientAccount = createAccount(recipientCard, Currency.USD, BigDecimal.valueOf(100));

        when(accountRepository.findByCard_CardNumber(senderCard)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByCard_CardNumber(recipientCard)).thenReturn(Optional.of(recipientAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = transactionService.withdraw(senderCard, recipientCard, amount, description);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(BigDecimal.valueOf(400), senderAccount.getBalance());
        assertEquals(BigDecimal.valueOf(200), recipientAccount.getBalance());
        verify(emailService).checkIfCodeIsVerified(anyString());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_Successful_DifferentCurrency() {
        // Arrange
        String senderCard = "1111";
        String recipientCard = "2222";
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal convertedAmount = BigDecimal.valueOf(90); // e.g. USD -> EUR

        Account senderAccount = createAccount(senderCard, Currency.USD, BigDecimal.valueOf(500));
        Account recipientAccount = createAccount(recipientCard, Currency.EUR, BigDecimal.valueOf(100));

        when(accountRepository.findByCard_CardNumber(senderCard)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByCard_CardNumber(recipientCard)).thenReturn(Optional.of(recipientAccount));
        when(currencyLoader.convert(amount, "USD", "EUR")).thenReturn(convertedAmount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = transactionService.withdraw(senderCard, recipientCard, amount, "Conversion");

        // Assert
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(BigDecimal.valueOf(400), senderAccount.getBalance()); // 500 - 100
        assertEquals(BigDecimal.valueOf(190), recipientAccount.getBalance()); // 100 + 90
        verify(currencyLoader).convert(amount, "USD", "EUR");
    }

    @Test
    void getAllTransactions() {
        // Arrange
        String accNum = "UA123456";
        Pageable pageable = mock(Pageable.class);
        when(transactionRepository.findAllByAccount_AccountNumberOrToAccount_AccountNumber(accNum, accNum, pageable))
                .thenReturn(List.of(new Transaction(), new Transaction()));

        // Act
        List<Transaction> result = transactionService.getAllTransactions(accNum, pageable);

        // Assert
        assertEquals(2, result.size());
    }

    private Account createAccount(String cardNum, Currency currency, BigDecimal balance) {
        AuthUSer authUser = new AuthUSer();
        authUser.setEmail("test@example.com");
        Customer customer = new Customer();
        customer.setAuthUser(authUser);

        Card card = new Card();
        card.setCardNumber(cardNum);

        Account account = new Account();
        account.setCard(card);
        account.setCurrencyCode(currency);
        account.setBalance(balance);
        account.setCustomer(customer);
        account.setStatus(AccountStatus.ACTIVE);
        account.setSentTransactions(new ArrayList<>());
        account.setReceivedTransactions(new ArrayList<>());

        return account;
    }
}