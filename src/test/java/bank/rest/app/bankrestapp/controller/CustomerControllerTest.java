package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.controller.payload.UpdateCustomerPassword;
import bank.rest.app.bankrestapp.dto.ChangeEmailRequestDTO;
import bank.rest.app.bankrestapp.dto.ChangePasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.UpdateCustomerDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import bank.rest.app.bankrestapp.service.CustomerService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    private CustomerFacade customerFacade;
    private CustomerService customerService;
    private DtoValidator dtoValidator;
    private CustomerController controller;
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        this.customerFacade = mock(CustomerFacade.class);
        this.customerService = mock(CustomerService.class);
        this.dtoValidator = mock(DtoValidator.class);
        this.bindingResult = mock(BindingResult.class);
        this.controller = new CustomerController(customerFacade, customerService, dtoValidator);
    }

    @Test
    void getCustomer_ShouldUseAuthenticatedUserAndReturnOk() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();
        final GetCustomerDTO dto = mock(GetCustomerDTO.class);
        when(customerFacade.getCustomer("user@example.com")).thenReturn(dto);

        final var response = controller.getCustomer(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(customerFacade).getCustomer("user@example.com");
    }

    @Test
    void changePassword_ShouldDelegateToFacadeAndReturnOk() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();
        final UpdateCustomerPassword payload = new UpdateCustomerPassword("oldPassword123", "newPassword123");

        final var response = controller.changePassword(user, payload, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerFacade).updatePassword(
                new UpdateCustomerDTO("user@example.com", "oldPassword123", "newPassword123"),
                bindingResult
        );
    }

    @Test
    void initPasswordChange_ShouldUseAuthenticatedUserAndReturnOk() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();

        final var response = controller.initPasswordChange(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerService).initPasswordChange("user@example.com");
    }

    @Test
    void changePasswordWithVerification_ShouldValidateAndDelegateToService() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();
        final ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("12345", "newPassword123");

        final var response = controller.changePasswordWithVerification(user, dto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(dtoValidator).validate(dto, bindingResult);
        verify(customerService).changePassword("user@example.com", "12345", "newPassword123");
    }

    @Test
    void initEmailChange_ShouldUseAuthenticatedUserAndReturnOk() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();

        final var response = controller.initEmailChange(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerService).initEmailChange("user@example.com");
    }

    @Test
    void changeEmailWithVerification_ShouldValidateAndDelegateToService() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();
        final ChangeEmailRequestDTO dto = new ChangeEmailRequestDTO("12345", "new@example.com");

        final var response = controller.changeEmailWithVerification(user, dto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(dtoValidator).validate(dto, bindingResult);
        verify(customerService).changeEmail("user@example.com", "12345", "new@example.com");
    }
}
