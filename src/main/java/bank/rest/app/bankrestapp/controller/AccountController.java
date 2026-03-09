package bank.rest.app.bankrestapp.controller;


import bank.rest.app.bankrestapp.controller.payload.CreateAccountPayload;
import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.facade.AccountFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/accounts")
@AllArgsConstructor
public final class AccountController {

    private final AccountFacade accountFacade;

    /**
     * Creates a new account for the authenticated customer.
     *
     * @param userDetails authenticated user details
     * @param createAccountPayload account creation payload
     * @param bindingResult validation result
     * @return response containing the created account DTO
     * @throws IllegalArgumentException if the payload is invalid or the account cannot be created
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(
            final @AuthenticationPrincipal UserDetails userDetails,
            final @RequestBody CreateAccountPayload createAccountPayload,
            final BindingResult bindingResult
            ) {
        final String customerEmail = userDetails.getUsername();
        final CreateAccountDTO createAccountDTO = new CreateAccountDTO(
                createAccountPayload.accountType().name(),
                createAccountPayload.currency(),
                customerEmail
        );

        final GetAccountDTO accountDTO = this.accountFacade.createAccount(createAccountDTO, bindingResult);

        return ResponseEntity
                .status(CREATED)
                .body(accountDTO);
    }
}
