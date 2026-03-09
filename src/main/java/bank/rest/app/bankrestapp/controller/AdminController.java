package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.controller.payload.AccountStatusPayload;
import bank.rest.app.bankrestapp.dto.AccountStatusDTO;
import bank.rest.app.bankrestapp.dto.get.CetCustomerDetailsForAdminDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountForAdminDTO;
import bank.rest.app.bankrestapp.facade.AccountFacade;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public final class AdminController {

    private final CustomerFacade customerFacade;
    private final AccountFacade accountFacade;

    /**
     * Returns administrative customer dashboard data.
     *
     * @return response containing customer details for administrators
     */
    @GetMapping("/list/users")
    public ResponseEntity<?> getAdminDashboard() {
        final List<CetCustomerDetailsForAdminDTO> cetCustomerDetailsForAdminDTO = customerFacade.getCetCustomerDetailsForAdmin();

        return ok(cetCustomerDetailsForAdminDTO);
    }

    /**
     * Updates the status of the specified account.
     *
     * @param accountId identifier of the account to update
     * @param status requested account status payload
     * @return response containing the updated account DTO
     * @throws java.util.NoSuchElementException if the account cannot be found
     */
    @PutMapping("/user/accounts/{accountId:\\d+}")
    public ResponseEntity<?> updateAccountStatus(@PathVariable final Integer accountId, final @RequestBody AccountStatusPayload status) {
        final AccountStatusDTO accountStatus = new AccountStatusDTO(accountId, status.status());

        final GetAccountForAdminDTO accountStatusDTO = this.accountFacade.updateAccountStatus(accountStatus);

        return ok(accountStatusDTO);
    }

}
