package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.controller.payload.ResetCustomerPassword;
import bank.rest.app.bankrestapp.controller.payload.UpdateCustomerPassword;
import bank.rest.app.bankrestapp.dto.ResetPasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.UpdateCustomerDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import bank.rest.app.bankrestapp.security.CustomerPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
            final @AuthenticationPrincipal CustomerPrincipal customerPrincipal) {
        final String customerEmail = customerPrincipal.getUsername();
        final GetCustomerDTO getCustomerDTO = this.customerFacade.getCustomer(customerEmail);

        return ok(getCustomerDTO);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> updatePassword(
            final @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            final @RequestBody ResetCustomerPassword resetCustomerPassword) {
        final String customerEmail = customerPrincipal.getUsername();
        final ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO(
                customerEmail,
                resetCustomerPassword.newPassword()
        );
        this.customerFacade.resetPassword(resetPasswordRequestDTO);

        return ok()
                .build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            final @AuthenticationPrincipal CustomerPrincipal customerPrincipal,
            final @RequestBody UpdateCustomerPassword updateCustomerPassword,
            final BindingResult bindingResult) {
        final String customerEmail = customerPrincipal.getUsername();
        final UpdateCustomerDTO updateCustomerDTO = new UpdateCustomerDTO(
                customerEmail,
                updateCustomerPassword.oldPassword(),
                updateCustomerPassword.newPassword()
        );

        this.customerFacade.updatePassword(updateCustomerDTO, bindingResult);

        return ok()
                .build();
    }
}