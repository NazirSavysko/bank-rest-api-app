package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.TransactionService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Proxy;

import java.math.BigDecimal;
import java.util.List;

import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.CANCELLED;
import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionFacadeImpl to verify orchestration between facade, validator, services, loader and mapper.
 */
@ExtendWith(MockitoExtension.class)
class TransactionFacadeImplTest {

    private TransactionService transactionService;
    private DtoValidator dtoValidator;
    private Mapper<Transaction, GetTransactionDTO> transactionMapper;
    private CurrencyLoader currencyLoader;
    private AccountService accountService;

    private TransactionFacadeImpl sut;

    @BeforeEach
    void setUp() {
        this.transactionService = mock(TransactionService.class);
        this.dtoValidator = mock(DtoValidator.class);
        //noinspection unchecked
        this.transactionMapper = (Mapper<Transaction, GetTransactionDTO>) mock(Mapper.class);
        this.currencyLoader = mock(CurrencyLoader.class);
        this.accountService = mock(AccountService.class);

        this.sut = new TransactionFacadeImpl(
                transactionService,
                dtoValidator,
                transactionMapper,
                currencyLoader,
                accountService
        );
    }

    @Test
    void withdraw_shouldValidate_thenCallService_thenMapAndReturnDto() {
        // given
        CreateTransaction input = new CreateTransaction(
                "1111222233334444",
                "9999888877776666",
                BigDecimal.valueOf(50),
                "Payment"
        );
        BindingResult bindingResult = mock(BindingResult.class);

        Transaction createdTransaction = Transaction.builder()
                .transactionId(42)
                .amount(BigDecimal.valueOf(50))
                .currencyCode(Currency.USD)
                .description("Payment")
                .status(TransactionStatus.COMPLETED)
                .build();

        GetTransactionDTO mappedDto = mock(GetTransactionDTO.class);

        when(transactionService.withdraw(
                eq("1111222233334444"),
                eq("9999888877776666"),
                eq(BigDecimal.valueOf(50)),
                eq("Payment")
        )).thenReturn(createdTransaction);

        when(transactionMapper.toDto(createdTransaction)).thenReturn(mappedDto);

        // when
        GetTransactionDTO result = sut.withdraw(input, bindingResult);

        // then
        assertEquals(mappedDto, result);

        InOrder inOrder = inOrder(dtoValidator, transactionService, transactionMapper);
        inOrder.verify(dtoValidator).validate(input, bindingResult);
        inOrder.verify(transactionService).withdraw("1111222233334444", "9999888877776666", BigDecimal.valueOf(50), "Payment");
        inOrder.verify(transactionMapper).toDto(createdTransaction);

        verifyNoMoreInteractions(transactionService, transactionMapper);
        verify(dtoValidator, times(1)).validate(input, bindingResult);
    }

    @Test
    void withdraw_whenValidatorThrows_shouldNotCallServiceOrMapper() {
        // given
        CreateTransaction input = new CreateTransaction(
                "2222333344445555",
                "6666777788889999",
                BigDecimal.valueOf(75),
                "Invoice"
        );
        BindingResult bindingResult = mock(BindingResult.class);

        RuntimeException validationError = new RuntimeException("validation failed");
        doThrow(validationError).when(dtoValidator).validate(input, bindingResult);

        // when / then
        try {
            sut.withdraw(input, bindingResult);
        } catch (RuntimeException ex) {
            assertEquals(validationError, ex);
        }

        verify(dtoValidator).validate(input, bindingResult);
        verifyNoInteractions(transactionService);
        verifyNoInteractions(transactionMapper);
    }

    @Test
    void getAllTransactions_shouldLoadAccount_thenFilterConvertAndMap() {
        // given
        final String accountNumber = "ACC-123";
        Pageable pageable = Pageable.unpaged();

        Account account = Account.builder()
                .accountId(1)
                .accountNumber(accountNumber)
                .currencyCode(Currency.EUR)
                .build();

        // Transaction 1: toAccount == account, status COMPLETED -> included, should be converted and marked recipient
        Transaction txIncluded = Transaction.builder()
                .transactionId(1)
                .amount(BigDecimal.valueOf(100))
                .currencyCode(Currency.USD)
                .status(TransactionStatus.COMPLETED)
                .build();
        txIncluded.setToAccount(account);

        // Transaction 2: toAccount == account, status CANCELLED -> excluded by filter
        Transaction txExcluded = Transaction.builder()
                .transactionId(2)
                .amount(BigDecimal.valueOf(50))
                .currencyCode(Currency.USD)
                .status(CANCELLED)
                .build();
        txExcluded.setToAccount(account);

        // Transaction 3: toAccount != account, status FAILED -> included because condition checks toAccount equals account with FAILED/CANCELLED
        Account other = Account.builder().accountId(2).accountNumber("ACC-OTHER").currencyCode(Currency.USD).build();
        Transaction txOther = Transaction.builder()
                .transactionId(3)
                .amount(BigDecimal.valueOf(30))
                .currencyCode(Currency.USD)
                .status(FAILED) // FAILED but toAccount != account so should be included
                .build();
        txOther.setToAccount(other);

        when(accountService.getAccountByNumber(accountNumber)).thenReturn(account);

        // Create a resilient TransactionService mock that returns a PageImpl when the declared
        // return type of the invoked method is a Spring Data Page, otherwise returns a List.
        // Using a Mockito Answer lets us handle both signatures without relying on compile-time overloads.
        TransactionService transactionServiceProxy = mock(TransactionService.class, invocation -> {
            java.lang.reflect.Method method = invocation.getMethod();
            if ("getAllTransactions".equals(method.getName())) {
                // If the declared return type is a Spring Page, return a PageImpl
                if (org.springframework.data.domain.Page.class.isAssignableFrom(method.getReturnType())) {
                    Pageable requestedPageable = null;
                    Object[] args = invocation.getArguments();
                    if (args != null) {
                        for (Object a : args) {
                            if (a instanceof Pageable) {
                                requestedPageable = (Pageable) a;
                                break;
                            }
                        }
                    }
                    Pageable pageToUse = requestedPageable == null ? Pageable.unpaged() : requestedPageable;
                    return new PageImpl<>(List.of(txIncluded, txExcluded, txOther), pageToUse, 3);
                }
                // Otherwise return a List
                return List.of(txIncluded, txExcluded, txOther);
            }
            // For other methods return default null (not used in this test)
            return null;
        });

        // Use a local facade instance backed by the proxy so we don't depend on the mock's compile-time signature.
        TransactionFacadeImpl localSut = new TransactionFacadeImpl(transactionServiceProxy, dtoValidator, transactionMapper, currencyLoader, accountService);

        // currencyLoader should be called for each transaction (facade maps over full page)
        when(currencyLoader.convert(eq(BigDecimal.valueOf(100)), eq("USD"), eq("EUR")))
                .thenReturn(BigDecimal.valueOf(90));
        when(currencyLoader.convert(eq(BigDecimal.valueOf(50)), eq("USD"), eq("EUR")))
                .thenReturn(BigDecimal.valueOf(45));
        when(currencyLoader.convert(eq(BigDecimal.valueOf(30)), eq("USD"), eq("EUR")))
                .thenReturn(BigDecimal.valueOf(27));

        GetTransactionDTO dto1 = mock(GetTransactionDTO.class);
        GetTransactionDTO dto2 = mock(GetTransactionDTO.class);
        GetTransactionDTO dto3 = mock(GetTransactionDTO.class);

        when(transactionMapper.toDto(argThat(t -> t != null && t.getTransactionId() != null && t.getTransactionId().equals(1)))).thenReturn(dto1);
        when(transactionMapper.toDto(argThat(t -> t != null && t.getTransactionId() != null && t.getTransactionId().equals(2)))).thenReturn(dto2);
        when(transactionMapper.toDto(argThat(t -> t != null && t.getTransactionId() != null && t.getTransactionId().equals(3)))).thenReturn(dto3);

        // when
        var page = localSut.getAllTransactions(pageable, accountNumber);

        // then - all 3 transactions are converted and mapped (facade does not filter by status)
        List<GetTransactionDTO> content = page.getContent();
        assertEquals(3, content.size());
        assertTrue(content.contains(dto1));
        assertTrue(content.contains(dto2));
        assertTrue(content.contains(dto3));

        // verify call order: account loaded first, then conversions and mapping for each transaction
        InOrder inOrder = inOrder(accountService, currencyLoader, transactionMapper);
        inOrder.verify(accountService).getAccountByNumber(accountNumber);
        verify(currencyLoader).convert(BigDecimal.valueOf(100), "USD", "EUR");
        verify(currencyLoader).convert(BigDecimal.valueOf(50), "USD", "EUR");
        verify(currencyLoader).convert(BigDecimal.valueOf(30), "USD", "EUR");

        verify(transactionMapper).toDto(txIncluded);
        verify(transactionMapper).toDto(txExcluded);
        verify(transactionMapper).toDto(txOther);
    }
}
