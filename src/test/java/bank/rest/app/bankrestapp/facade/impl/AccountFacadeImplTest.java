package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountFacadeImplTest {

    private AccountService accountService;
    private DtoValidator dtoValidator;
    private Mapper<Account, GetAccountDTO> accountMapper;

    private AccountFacadeImpl sut;

    @BeforeEach
    void setUp() {
        this.accountService = mock(AccountService.class);
        this.dtoValidator = mock(DtoValidator.class);
        //noinspection unchecked
        this.accountMapper = (Mapper<Account, GetAccountDTO>) mock(Mapper.class);

        this.sut = new AccountFacadeImpl(accountService, dtoValidator, accountMapper);
    }

    @Test
    void createAccount_shouldValidate_thenCallService_thenMapAndReturnDto() {
        // given
        CreateAccountDTO input = new CreateAccountDTO("USD", "john.doe@example.com");
        BindingResult bindingResult = mock(BindingResult.class);

        Account createdAccount = Account.builder()
                .accountId(123)
                .accountNumber("ACC-1")
                .build();

        GetAccountDTO mappedDto = mock(GetAccountDTO.class);

        when(accountService.createAccount("USD", "john.doe@example.com"))
                .thenReturn(createdAccount);
        when(accountMapper.toDto(createdAccount))
                .thenReturn(mappedDto);

        // when
        GetAccountDTO result = sut.createAccount(input, bindingResult);

        // then
        assertEquals(mappedDto, result);

        InOrder inOrder = inOrder(dtoValidator, accountService, accountMapper);
        inOrder.verify(dtoValidator).validate(input, bindingResult);
        inOrder.verify(accountService).createAccount("USD", "john.doe@example.com");
        inOrder.verify(accountMapper).toDto(createdAccount);

        verifyNoMoreInteractions(accountService, accountMapper);
        // dtoValidator may have other internal interactions, but from facade POV we only care it was called once with args:
        verify(dtoValidator, times(1)).validate(input, bindingResult);
    }

    @Test
    void createAccount_whenValidatorThrows_shouldNotCallServiceOrMapper() {
        // given
        CreateAccountDTO input = new CreateAccountDTO("USD", "john.doe@example.com");
        BindingResult bindingResult = mock(BindingResult.class);

        RuntimeException validationError = new RuntimeException("validation failed");
        doThrow(validationError).when(dtoValidator).validate(input, bindingResult);

        // when / then
        try {
            sut.createAccount(input, bindingResult);
        } catch (RuntimeException ex) {
            assertEquals(validationError, ex);
        }

        verify(dtoValidator).validate(input, bindingResult);
        verifyNoInteractions(accountService);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void createAccount_shouldPassThroughExactDtoFieldsToService() {
        // given
        CreateAccountDTO input = new CreateAccountDTO("EUR", "alice@example.com");
        BindingResult bindingResult = mock(BindingResult.class);

        Account createdAccount = Account.builder().accountId(7).build();
        GetAccountDTO mappedDto = mock(GetAccountDTO.class);

        when(accountService.createAccount(any(), any())).thenReturn(createdAccount);
        when(accountMapper.toDto(createdAccount)).thenReturn(mappedDto);

        // when
        sut.createAccount(input, bindingResult);

        // then
        verify(accountService).createAccount("EUR", "alice@example.com");
        verify(accountMapper).toDto(createdAccount);
    }
}
