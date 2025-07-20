package bank.rest.app.bankrestapp.controller;


import bank.rest.app.bankrestapp.controller.payload.CreateAccountPayload;
import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.facade.AccountFacade;
import bank.rest.app.bankrestapp.security.CustomerPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/accounts")
public final class AccountController {

    private final AccountFacade accountFacade;


    @Autowired
    public AccountController(final AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(
            final @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            final @RequestBody CreateAccountPayload createAccountPayload,
            final BindingResult bindingResult
            ) {
        final String customerEmail = customerPrincipal.getUsername();
        final CreateAccountDTO createAccountDTO = new CreateAccountDTO(
                createAccountPayload.accountType(),
                customerEmail
        );

        final GetAccountDTO accountDTO = this.accountFacade.createAccount(createAccountDTO, bindingResult);

        return ResponseEntity
                .status(CREATED)
                .body(accountDTO);
    }
}
