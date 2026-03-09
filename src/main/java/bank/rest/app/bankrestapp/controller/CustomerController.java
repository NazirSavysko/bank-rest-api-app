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

    /**
     * Returns profile data for the authenticated customer.
     *
     * @param userDetails authenticated user details
     * @return response containing customer data
     * @throws java.util.NoSuchElementException if the customer cannot be found
     */
    @GetMapping("/customer")
    public ResponseEntity<?> getCustomer(
            final @AuthenticationPrincipal UserDetails userDetails) {
        final String customerEmail = userDetails.getUsername();
        final GetCustomerDTO getCustomerDTO = this.customerFacade.getCustomer(customerEmail);

        return ok(getCustomerDTO);
    }

    /**
     * Resets a customer password using the forgot-password flow.
     *
     * @param resetPasswordRequestDTO reset-password payload
     * @param bindingResult validation result
     * @return empty success response
     * @throws IllegalArgumentException if validation fails or the password cannot be reset
     */
    @PutMapping("/forgot-password")
    public ResponseEntity<?> updatePassword(final @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO,final BindingResult bindingResult) {
        this.customerFacade.resetPassword(resetPasswordRequestDTO,bindingResult);

        return ok()
                .build();
    }

    /**
     * Changes the password of the authenticated customer.
     *
     * @param userDetails authenticated user details
     * @param updateCustomerPassword change-password payload
     * @param bindingResult validation result
     * @return empty success response
     * @throws IllegalArgumentException if validation fails or password rules are violated
     */
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
