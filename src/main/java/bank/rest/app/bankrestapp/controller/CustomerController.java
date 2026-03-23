package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.controller.payload.UpdateCustomerPassword;
import bank.rest.app.bankrestapp.dto.ChangeEmailRequestDTO;
import bank.rest.app.bankrestapp.dto.ChangePasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.ResetPasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.UpdateCustomerDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import bank.rest.app.bankrestapp.service.CustomerService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import lombok.AllArgsConstructor;
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
    private final CustomerService customerService;
    private final DtoValidator dtoValidator;

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

    @PostMapping("/me/settings/password/init")
    public ResponseEntity<?> initPasswordChange(final @AuthenticationPrincipal UserDetails userDetails) {
        this.customerService.initPasswordChange(userDetails.getUsername());
        return ok().build();
    }

    @PostMapping("/me/settings/password/change")
    public ResponseEntity<?> changePasswordWithVerification(
            final @AuthenticationPrincipal UserDetails userDetails,
            final @RequestBody ChangePasswordRequestDTO requestDTO,
            final BindingResult bindingResult) {
        this.dtoValidator.validate(requestDTO, bindingResult);
        this.customerService.changePassword(
                userDetails.getUsername(),
                requestDTO.verificationCode(),
                requestDTO.newPassword()
        );
        return ok().build();
    }

    @PostMapping("/me/settings/email/init")
    public ResponseEntity<?> initEmailChange(final @AuthenticationPrincipal UserDetails userDetails) {
        this.customerService.initEmailChange(userDetails.getUsername());
        return ok().build();
    }

    @PostMapping("/me/settings/email/change")
    public ResponseEntity<?> changeEmailWithVerification(
            final @AuthenticationPrincipal UserDetails userDetails,
            final @RequestBody ChangeEmailRequestDTO requestDTO,
            final BindingResult bindingResult) {
        this.dtoValidator.validate(requestDTO, bindingResult);
        this.customerService.changeEmail(
                userDetails.getUsername(),
                requestDTO.verificationCode(),
                requestDTO.newEmail()
        );
        return ok().build();
    }
}
