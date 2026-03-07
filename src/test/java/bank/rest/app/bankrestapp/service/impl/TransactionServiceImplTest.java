package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.get.TransactionHistoryDirection;
import bank.rest.app.bankrestapp.dto.get.TransactionHistoryType;
import bank.rest.app.bankrestapp.entity.*;
import bank.rest.app.bankrestapp.entity.enums.AccountStatus;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.resository.projection.TransactionHistoryProjection;
import bank.rest.app.bankrestapp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        Page<Transaction> page = new PageImpl<>(List.of(new Transaction(), new Transaction()));

        when(transactionRepository.findAllTransactions(eq(accNum), anyList(), eq(pageable)))
                .thenReturn(page);

        // Act
        Page<Transaction> result = transactionService.getAllTransactions(accNum, pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void getTransactionHistory_shouldMapProjectionAndDirection() {
        // Arrange
        Integer accountId = 10;
        Pageable pageable = Pageable.unpaged();

        final Account account = new Account();
        account.setAccountId(accountId);

        TransactionHistoryProjection transferIncome = mock(TransactionHistoryProjection.class);
        when(transferIncome.getOperationId()).thenReturn(101L);
        when(transferIncome.getItemType()).thenReturn(TransactionHistoryType.TRANSFER.name());
        when(transferIncome.getSenderAccountId()).thenReturn(33);
        when(transferIncome.getAmount()).thenReturn(BigDecimal.valueOf(50));
        when(transferIncome.getCurrencyCode()).thenReturn("USD");
        when(transferIncome.getStatus()).thenReturn("COMPLETED");
        when(transferIncome.getDescription()).thenReturn("Incoming transfer");

        TransactionHistoryProjection ibanExpense = mock(TransactionHistoryProjection.class);
        when(ibanExpense.getOperationId()).thenReturn(102L);
        when(ibanExpense.getItemType()).thenReturn(TransactionHistoryType.IBAN_PAYMENT.name());
        when(ibanExpense.getSenderAccountId()).thenReturn(accountId);
        when(ibanExpense.getAmount()).thenReturn(BigDecimal.valueOf(15));
        when(ibanExpense.getCurrencyCode()).thenReturn("UAH");
        when(ibanExpense.getStatus()).thenReturn("COMPLETED");
        when(ibanExpense.getRecipientIban()).thenReturn("UA123456789012345678901234567");
        when(ibanExpense.getRecipientName()).thenReturn("Recipient Name");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findAccountHistory(accountId, pageable))
                .thenReturn(new PageImpl<>(List.of(transferIncome, ibanExpense), pageable, 2));

        // Act
        final var result = transactionService.getTransactionHistory(accountId, pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals(TransactionHistoryDirection.INCOME, result.getContent().get(0).direction());
        assertEquals(TransactionHistoryType.TRANSFER, result.getContent().get(0).type());
        assertEquals(TransactionHistoryDirection.EXPENSE, result.getContent().get(1).direction());
        assertEquals("UA123456789012345678901234567", result.getContent().get(1).recipientIban());
        verify(accountRepository).findById(accountId);
        verify(transactionRepository).findAccountHistory(accountId, pageable);
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
