package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
