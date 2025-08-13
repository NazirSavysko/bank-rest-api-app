package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.controller.payload.UpdateCustomerPassword;
import bank.rest.app.bankrestapp.dto.ResetPasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.UpdateCustomerDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/customers")
@AllArgsConstructor
class CustomerController {

    private final CustomerFacade customerFacade;

    @GetMapping("/customer")
    public ResponseEntity<?> getCustomer(
            final @AuthenticationPrincipal UserDetails userDetails) {
        final String customerEmail = userDetails.getUsername();
        final GetCustomerDTO getCustomerDTO = this.customerFacade.getCustomer(customerEmail);

        return ok(getCustomerDTO);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> updatePassword(final @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        this.customerFacade.resetPassword(resetPasswordRequestDTO);

        return ok()
                .build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            final @AuthenticationPrincipal UserDetails userDetails,
            final @RequestBody UpdateCustomerPassword updateCustomerPassword,
            final BindingResult bindingResult) {
        final String customerEmail = userDetails.getUsername();
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