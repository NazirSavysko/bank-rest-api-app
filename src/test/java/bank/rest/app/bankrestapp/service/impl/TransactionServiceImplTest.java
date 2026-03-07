package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.entity.*;
import bank.rest.app.bankrestapp.dto.get.TransactionHistoryItemDTO;
import bank.rest.app.bankrestapp.entity.enums.AccountStatus;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.HistoryDirection;
import bank.rest.app.bankrestapp.entity.enums.HistoryFilter;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.resository.projection.HistoryItemProjection;
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
import java.time.LocalDateTime;
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
    void getTransactionHistory_shouldMapDirectionAndProjection() {
        // Arrange
        Integer accountId = 5;
        Pageable pageable = Pageable.unpaged();
        LocalDateTime now = LocalDateTime.now();

        HistoryItemProjection outgoing = new TestHistoryItemProjection(
                1, BigDecimal.TEN, "USD", now,
                "TRANSFER", accountId, 42, "transfer out");
        HistoryItemProjection incoming = new TestHistoryItemProjection(
                2, BigDecimal.ONE, "USD", now.minusHours(1),
                "TRANSFER", 99, accountId, "transfer in");
        HistoryItemProjection payment = new TestHistoryItemProjection(
                3, BigDecimal.valueOf(7), "USD", now.minusHours(2),
                "IBAN_PAYMENT", accountId, null, "iban payment");

        when(transactionRepository.findAccountHistory(eq(accountId), eq("ALL"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(outgoing, incoming, payment), pageable, 3));

        // Act
        Page<TransactionHistoryItemDTO> result = transactionService.getTransactionHistory(accountId, HistoryFilter.ALL, pageable);

        // Assert
        assertEquals(3, result.getTotalElements());
        assertEquals(HistoryDirection.EXPENSE, result.getContent().get(0).direction());
        assertEquals(HistoryDirection.INCOME, result.getContent().get(1).direction());
        assertEquals(HistoryDirection.EXPENSE, result.getContent().get(2).direction());
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

    private record TestHistoryItemProjection(
            Integer id,
            BigDecimal amount,
            String currency,
            LocalDateTime createdAt,
            String type,
            Integer senderAccountId,
            Integer receiverAccountId,
            String details
    ) implements HistoryItemProjection {}
}
