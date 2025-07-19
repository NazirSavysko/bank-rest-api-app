package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import bank.rest.app.bankrestapp.security.CustomerPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/users")
class CustomerController {

    private final CustomerFacade customerFacade;

    @Autowired
    public CustomerController(final CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCustomer(
            final @AuthenticationPrincipal CustomerPrincipal customerPrincipal){
        final String customerEmail = customerPrincipal.getUsername();
        final GetCustomerDTO getCustomerDTO =  this.customerFacade.getCustomer(customerEmail);

        return ok(getCustomerDTO);
    }
}