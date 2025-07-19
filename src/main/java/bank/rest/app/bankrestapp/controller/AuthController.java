package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.AuthenticateDTO;
import bank.rest.app.bankrestapp.dto.CreateCustomerDTO;
import bank.rest.app.bankrestapp.dto.LoginDTO;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static jakarta.mail.event.FolderEvent.CREATED;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/")
class AuthController {

    private final CustomerFacade customerFacade;

    @Autowired
    public AuthController(final CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(final @RequestBody CreateCustomerDTO createCustomerDTO) {

        this.customerFacade.register(createCustomerDTO);

        return ResponseEntity
                .status(CREATED)
                .build();
    }

    @PostMapping(path = "/log-in")
    public ResponseEntity<?> login(final @RequestBody LoginDTO loginDTO) {
      final AuthenticateDTO authenticated  = this.customerFacade.authenticate(loginDTO);

        return ok(authenticated);
    }

}
