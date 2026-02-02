package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.*;
import bank.rest.app.bankrestapp.entity.enums.Role;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.resository.CustomerRoleRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.CardService;
import bank.rest.app.bankrestapp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.constants.MessageError.*;
import static bank.rest.app.bankrestapp.entity.enums.Role.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CustomerRoleRepository customerRoleRepository;
    @Mock private AccountService accountService;
    @Mock private CardService cardService;
    @Mock private EmailService emailService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void login_Success() {
        String email = "test@example.com";
        Customer customer = new Customer();
        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));

        Customer result = customerService.login(email);
        assertEquals(customer, result);
    }

    @Test
    void register_Success() {
        // Arrange
        String first = "John", last = "Doe", email = "john@example.com", pass = "pass", phone = "123";

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.empty());
        when(customerRepository.findAllByPhone(phone)).thenReturn(Optional.empty());
        when(customerRoleRepository.findByRoleName(ROLE_USER)).thenReturn(Optional.of(new CustomerRole()));
        when(passwordEncoder.encode(pass)).thenReturn("encodedPass");
        when(accountService.generateAccountByCurrencyCode(any())).thenReturn(new Account());
        when(cardService.generateCard()).thenReturn(new Card());

        // Act
        customerService.register(first, last, email, pass, phone);

        // Assert
        verify(emailService).sendVerificationCode(email);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void register_EmailExists() {
        String email = "john@example.com";
        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(new Customer()));

        assertThrows(IllegalArgumentException.class, () ->
            customerService.register("John", "Doe", email, "pass", "123")
        );
    }

    @Test
    void checkIfAuthenticated_Success() {
        String email = "test@example.com";
        String pass = "pass";
        Customer customer = new Customer();
        AuthUSer authUser = new AuthUSer();
        authUser.setEmail(email);
        authUser.setPasswordHash("hashedPass");
        customer.setAuthUser(authUser);

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(pass, "hashedPass")).thenReturn(true);

        Customer result = customerService.checkIfAuthenticated(email, pass);
        assertNotNull(result);
    }

    @Test
    void resetPassword_Success() {
        String email = "test@example.com";
        String newPass = "newPass";
        Customer customer = new Customer();
        AuthUSer authUser = new AuthUSer();
        authUser.setEmail(email);
        authUser.setPasswordHash("oldHash"); // Not equals to newPass
        customer.setAuthUser(authUser);

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode(newPass)).thenReturn("newHash");

        customerService.resetPassword(email, newPass);

        verify(emailService).checkIfCodeIsVerified(email);
        verify(customerRepository).save(customer);
        assertEquals("newHash", customer.getAuthUser().getPasswordHash());
    }

    @Test
    void updatePassword_Success() {
        String email = "test@example.com";
        String oldPass = "oldPass";
        String newPass = "newPass";

        Customer customer = new Customer();
        AuthUSer authUser = new AuthUSer();
        authUser.setEmail(email);
        authUser.setPasswordHash("hashedOldPass");
        customer.setAuthUser(authUser);

        when(customerRepository.findByAuthUserEmail(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(oldPass, "hashedOldPass")).thenReturn(true);
        when(passwordEncoder.encode(newPass)).thenReturn("hashedNewPass");

        customerService.updatePassword(email, newPass, oldPass);

        verify(emailService).checkIfCodeIsVerified(email);
        verify(customerRepository).save(customer);
    }

    @Test
    void getAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(new Customer()));
        List<Customer> list = customerService.getAllCustomers();
        assertEquals(1, list.size());
    }
}