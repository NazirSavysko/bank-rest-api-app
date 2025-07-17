package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateCustomerDTO;
import bank.rest.app.bankrestapp.dto.LoginDTO;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/users")
class CustomersController {

    private final CustomerFacade customerFacade;

    @Autowired
    public CustomersController(final CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(final @RequestBody CreateCustomerDTO createCustomerDTO ) {

        this.customerFacade.register(createCustomerDTO);

        return ResponseEntity
                .ok("");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(final @RequestBody LoginDTO loginDTO) {

        this.customerFacade.login(loginDTO);


        return ResponseEntity
                .ok("Login successful");
    }


}
