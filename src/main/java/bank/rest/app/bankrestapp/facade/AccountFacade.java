package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.AccountStatusDTO;
import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountForAdminDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface AccountFacade {

    GetAccountDTO createAccount(CreateAccountDTO createAccountDTO, BindingResult bindingResult);

    GetAccountForAdminDTO updateAccountStatus(AccountStatusDTO accountStatus);
}
