package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.*;
import bank.rest.app.bankrestapp.dto.get.AuthenticateDTO;
import bank.rest.app.bankrestapp.dto.get.CetCustomerDetailsForAdminDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface CustomerFacade {

    /**
     * Loads customer details for the authenticated user and maps them to a DTO.
     *
     * @param customerEmail authenticated customer email
     * @return customer DTO
     * @throws java.util.NoSuchElementException if the customer cannot be found
     */
    GetCustomerDTO getCustomer(final String customerEmail);

    /**
     * Validates the registration payload and delegates customer creation to the service layer.
     *
     * @param createCustomerDTO registration payload
     * @param bindingResult validation result
     * @throws IllegalArgumentException if validation fails or the customer cannot be registered
     */
    void register(CreateCustomerDTO createCustomerDTO, final BindingResult bindingResult);

    /**
     * Authenticates a customer and maps the authentication result to a DTO.
     *
     * @param loginDTO login request payload
     * @param bindingResult validation result
     * @return authentication DTO
     * @throws IllegalArgumentException if validation or authentication fails
     */
    AuthenticateDTO authenticate(LoginDTO loginDTO, final BindingResult bindingResult);

    /**
     * Resets a customer password after validating the request payload.
     *
     * @param resetPasswordRequestDTO reset-password payload
     * @param bindingResult validation result
     * @throws IllegalArgumentException if validation fails or the new password is invalid
     */
    void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO, final BindingResult bindingResult);


    /**
     * Changes the authenticated customer's password after request validation.
     *
     * @param updateCustomerDTO password-update payload
     * @param bindingResult validation result
     * @throws IllegalArgumentException if validation fails or password rules are violated
     */
    void updatePassword(UpdateCustomerDTO updateCustomerDTO, BindingResult bindingResult);

    /**
     * Returns customer details for administrative dashboards.
     *
     * @return list of customer DTOs for administrators
     */
    List<CetCustomerDetailsForAdminDTO> getCetCustomerDetailsForAdmin();
}
