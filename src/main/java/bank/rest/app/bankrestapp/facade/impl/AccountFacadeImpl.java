package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.facade.AccountFacade;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import static bank.rest.app.bankrestapp.utils.MapperUtils.mapDto;

@Component
public class AccountFacadeImpl implements AccountFacade {

    private final AccountService accountService;
    private final DtoValidator dtoValidator;
    private final Mapper<Account, GetAccountDTO> accountMapper;

    @Autowired
    public AccountFacadeImpl(final AccountService accountService,
                             final Mapper<Account, GetAccountDTO> accountMapper,
                             final DtoValidator dtoValidator) {
        this.accountService = accountService;
        this.dtoValidator = dtoValidator;
        this.accountMapper = accountMapper;
    }

    @Override
    public GetAccountDTO createAccount(final @NotNull CreateAccountDTO createAccountDTO,
                                       final BindingResult bindingResult) {
        this.dtoValidator.validate(createAccountDTO, bindingResult);

        final Account account = this.accountService.createAccount(
                createAccountDTO.accountType(),
                createAccountDTO.customerEmail()
        );

        return mapDto(account, this.accountMapper::toDto);
    }
}
