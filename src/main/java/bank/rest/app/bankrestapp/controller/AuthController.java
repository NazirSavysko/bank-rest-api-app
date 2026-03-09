package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.get.AuthenticateDTO;
import bank.rest.app.bankrestapp.dto.CreateCustomerDTO;
import bank.rest.app.bankrestapp.dto.LoginDTO;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
class AuthController {

    private final CustomerFacade customerFacade;

    /**
     * Registers a new customer.
     *
     * @param createCustomerDTO customer registration payload
     * @param bindingResult validation result
     * @return empty response with creation status
     * @throws IllegalArgumentException if validation fails or registration cannot be completed
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(final @RequestBody CreateCustomerDTO createCustomerDTO,
                                      final BindingResult bindingResult) {

        this.customerFacade.register(createCustomerDTO, bindingResult);

        return ResponseEntity
                .status(CREATED)
                .build();
    }

    /**
     * Authenticates a customer and returns the authentication payload.
     *
     * @param loginDTO login request payload
     * @param bindingResult validation result
     * @return response containing authentication data
     * @throws IllegalArgumentException if validation or authentication fails
     */
    @PostMapping(path = "/log-in")
    public ResponseEntity<?> login(final @RequestBody LoginDTO loginDTO,
                                   final BindingResult bindingResult) {
      final AuthenticateDTO authenticated  = this.customerFacade.authenticate(loginDTO, bindingResult);

        return ok(authenticated);
    }

}
