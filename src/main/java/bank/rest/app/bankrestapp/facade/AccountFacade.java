package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.AccountStatusDTO;
import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountForAdminDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface AccountFacade {

    /**
     * Validates the account-creation request, delegates account creation to the service layer,
     * and maps the result to the response DTO.
     *
     * @param createAccountDTO account creation request data
     * @param bindingResult validation result for the request
     * @return created account DTO
     * @throws IllegalArgumentException if validation fails or the customer cannot create the requested account
     */
    GetAccountDTO createAccount(CreateAccountDTO createAccountDTO, BindingResult bindingResult);

    /**
     * Updates an account status for administrative use cases.
     *
     * @param accountStatus requested account status change
     * @return updated account DTO for administrators
     * @throws java.util.NoSuchElementException if the target account cannot be found
     */
    GetAccountForAdminDTO updateAccountStatus(AccountStatusDTO accountStatus);
}
