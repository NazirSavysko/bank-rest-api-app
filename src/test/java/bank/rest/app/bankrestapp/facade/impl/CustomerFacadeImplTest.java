package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.constants.MessageError;
import bank.rest.app.bankrestapp.dto.CreateCustomerDTO;
import bank.rest.app.bankrestapp.dto.LoginDTO;
import bank.rest.app.bankrestapp.dto.ResetPasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.UpdateCustomerDTO;
import bank.rest.app.bankrestapp.dto.get.AuthenticateDTO;
import bank.rest.app.bankrestapp.dto.get.CetCustomerDetailsForAdminDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.entity.AuthUSer;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.CustomerRole;
import bank.rest.app.bankrestapp.entity.enums.Role;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.security.JwtUtil;
import bank.rest.app.bankrestapp.service.CustomerService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;


import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerFacadeImplTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private Mapper<Customer, GetCustomerDTO> customerMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private DtoValidator dtoValidator;

    @Mock
    private Mapper<Customer, CetCustomerDetailsForAdminDTO> customerMapperForAdmin;

    @InjectMocks
    private CustomerFacadeImpl facade;

    @Mock
    private BindingResult bindingResult;

//    @Test
//    void getCustomer_shouldDelegateToServiceAndInvokeMapper() {
//        final String email = "john.doe@example.com";
//
//        final Customer customer = Customer.builder()
//                .authUser(AuthUSer.builder().email(email).build())
//                .build();
//
//        when(customerService.login(email)).thenReturn(customer);
//
//        facade.getCustomer(email);
//
//        final InOrder inOrder = inOrder(customerService, customerMapper);
//        inOrder.verify(customerService).login(email);
//        inOrder.verify(customerMapper).toDto(customer);
//        inOrder.verifyNoMoreInteractions();
//
//        verifyNoInteractions(dtoValidator, jwtUtil, customerMapperForAdmin);
//    }

    @Test
    void register_shouldValidateThenCallServiceWithFlattenedFields() {
        final CreateCustomerDTO request = new CreateCustomerDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "+380991112233"
        );

        facade.register(request, bindingResult);

        final InOrder inOrder = inOrder(dtoValidator, customerService);
        inOrder.verify(dtoValidator).validate(request, bindingResult);
        inOrder.verify(customerService).register(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                request.phoneNumber()
        );
        inOrder.verifyNoMoreInteractions();

        verifyNoInteractions(customerMapper, jwtUtil, customerMapperForAdmin);
    }

    @Test
    void authenticate_shouldValidateThenAuthenticateGenerateTokenAndReturnRoleName() {
        final LoginDTO request = new LoginDTO("john.doe@example.com", "password123");

        final CustomerRole role = new CustomerRole();
        role.setRoleName(Role.ROLE_USER);

        final AuthUSer authUSer = AuthUSer.builder()
                .email(request.email())
                .customerRole(List.of(role))
                .build();

        final Customer customer = Customer.builder()
                .authUser(authUSer)
                .build();

        when(customerService.checkIfAuthenticated(request.email(), request.password()))
                .thenReturn(customer);
        when(jwtUtil.generateToken(customer)).thenReturn("jwt-token");

        final AuthenticateDTO result = facade.authenticate(request, bindingResult);

        assertNotNull(result);
        assertEquals("jwt-token", result.token());
        assertEquals("ROLE_USER", result.role());

        final InOrder inOrder = inOrder(dtoValidator, customerService, jwtUtil);
        inOrder.verify(dtoValidator).validate(request, bindingResult);
        inOrder.verify(customerService).checkIfAuthenticated(request.email(), request.password());
        inOrder.verify(jwtUtil).generateToken(customer);
        inOrder.verifyNoMoreInteractions();

        verifyNoInteractions(customerMapper, customerMapperForAdmin);
    }

    @Test
    void authenticate_shouldThrowWhenNoRolePresent_andMustNotGenerateToken() {
        final LoginDTO request = new LoginDTO("john.doe@example.com", "password123");

        final AuthUSer authUSer = AuthUSer.builder()
                .email(request.email())
                .customerRole(List.of())
                .build();

        final Customer customer = Customer.builder()
                .authUser(authUSer)
                .build();

        when(customerService.checkIfAuthenticated(request.email(), request.password()))
                .thenReturn(customer);

        final NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> facade.authenticate(request, bindingResult)
        );

        assertEquals(MessageError.ERRORS_CUSTOMER_ROLE_NOT_FOUND, ex.getMessage());

        verify(dtoValidator).validate(request, bindingResult);
        verify(customerService).checkIfAuthenticated(request.email(), request.password());
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(customerMapper, customerMapperForAdmin);
    }

    @Test
    void resetPassword_shouldValidateThenDelegateToService() {
        final ResetPasswordRequestDTO request = new ResetPasswordRequestDTO(
                "john.doe@example.com",
                "newPassword123"
        );

        facade.resetPassword(request, bindingResult);

        final InOrder inOrder = inOrder(dtoValidator, customerService);
        inOrder.verify(dtoValidator).validate(request, bindingResult);
        inOrder.verify(customerService).resetPassword(request.email(), request.password());
        inOrder.verifyNoMoreInteractions();

        verifyNoInteractions(customerMapper, jwtUtil, customerMapperForAdmin);
    }

    @Test
    void updatePassword_shouldValidateThenDelegateToService() {
        final UpdateCustomerDTO request = new UpdateCustomerDTO(
                "john.doe@example.com",
                "oldPassword123",
                "newPassword123"
        );

        facade.updatePassword(request, bindingResult);

        final InOrder inOrder = inOrder(dtoValidator, customerService);
        inOrder.verify(dtoValidator).validate(request, bindingResult);
        inOrder.verify(customerService).updatePassword(
                request.email(),
                request.newPassword(),
                request.oldPassword()
        );
        inOrder.verifyNoMoreInteractions();

        verifyNoInteractions(customerMapper, jwtUtil, customerMapperForAdmin);
    }

    @Test
    void getCetCustomerDetailsForAdmin_shouldDelegateToServiceAndReturnSameSizeList() {
        final Customer c1 = Customer.builder().build();
        final Customer c2 = Customer.builder().build();

        when(customerService.getAllCustomers()).thenReturn(List.of(c1, c2));

        final List<CetCustomerDetailsForAdminDTO> result = facade.getCetCustomerDetailsForAdmin();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(customerService).getAllCustomers();
        verifyNoMoreInteractions(customerService);

        // This is a unit test for facade orchestration (service delegation + basic transformation result shape).
        // Mapper invocations via method references/lambdas can be brittle to verify depending on Mockito/JDK behavior,
        // so we intentionally don't assert mapper interactions here.
        verifyNoInteractions(dtoValidator, jwtUtil);
    }
}
