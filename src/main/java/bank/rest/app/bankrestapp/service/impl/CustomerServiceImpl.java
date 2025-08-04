package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.*;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.resository.CustomerRoleRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.CardService;
import bank.rest.app.bankrestapp.service.CustomerService;
import bank.rest.app.bankrestapp.service.EmailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.constants.AccountDefaults.DEFAULT_CURRENCY;
import static bank.rest.app.bankrestapp.entity.enums.Role.ROLE_USER;
import static bank.rest.app.bankrestapp.constants.MessageError.*;
import static java.time.LocalDateTime.now;
import static java.util.List.of;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRoleRepository customerRoleRepository;
    private final AccountService accountService;
    private final CardService cardService;
    private final EmailService emailService;

    @Autowired
    public CustomerServiceImpl(final CustomerRepository customerRepository,
                               final PasswordEncoder passwordEncoder,
                               final CustomerRoleRepository customerRoleRepository,
                               final AccountService accountService,
                               final CardService cardService,
                               final EmailService emailService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRoleRepository = customerRoleRepository;
        this.accountService = accountService;
        this.cardService = cardService;
        this.emailService = emailService;
    }

    @Override
    public @NotNull Customer login(final String email) {
        return this.getCustomerByEmail(email);
    }

    @Override
    public void register(final String firstName,
                         final String lastName,
                         final String email,
                         final String password,
                         final String phoneNumber) {
        if (customerRepository.findByAuthUserEmail(email).isPresent()) {
            throw new IllegalArgumentException(ERRORS_EMAIL_ALREADY_EXISTS);
        }
        if (customerRepository.findAllByPhone(phoneNumber).isPresent()) {
            throw new IllegalArgumentException(ERRORS_PHONE_NUMBER_ALREADY_EXISTS);
        }
        this.emailService.sendVerificationCode(email);

        final CustomerRole customerRole = this.customerRoleRepository.findByRoleName(ROLE_USER).orElseThrow(
                () -> new NoSuchElementException(ERRORS_CUSTOMER_ROLE_NOT_FOUND)
        );

        final AuthUSer authUser = AuthUSer.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .customerRole(of(customerRole))
                .createdAt(now())
                .build();
        final Account account = this.accountService.generateAccountByCurrencyCode(DEFAULT_CURRENCY);
        final Card card = this.cardService.generateCard();

        final Customer customer = Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phoneNumber)
                .authUser(authUser)
                .createdAt(now())
                .accounts(of(account))
                .build();

        account.setCustomer(customer);
        account.setCard(card);
        card.setAccount(account);
        authUser.setCustomer(customer);
        authUser.setCustomerRole(of(customerRole));

        this.customerRepository.save(customer);
    }

    @Override
    public @NotNull Customer checkIfAuthenticated(final String email, final String password) {
        return this.checkAuthentication(email, password, ERRORS_INVALID_PASSWORD);
    }

    @Override
    public void resetPassword(final String email, final @NotNull String password) {
        final Customer customer = this.getCustomerByEmail(email);

        if (password.equals(customer.getAuthUser().getPasswordHash())) {
            throw new IllegalArgumentException(ERRORS_INVALID_NEW_PASSWORD);
        }

        this.changePassword(customer, password);
    }

    @Override
    public void updatePassword(final String email, final @NotNull String newPassword, final String oldPassword) {
        if (newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException(ERRORS_INVALID_NEW_PASSWORD);
        }

        final Customer customer = this.checkAuthentication(email, oldPassword, ERRORS_INVALID_OLD_PASSWORD);

        this.changePassword(customer, newPassword);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    private void changePassword(final @NotNull Customer customer, final String newPassword) {
        this.emailService.checkIfCodeIsVerified(customer.getAuthUser().getEmail());

        customer.getAuthUser().setPasswordHash(passwordEncoder.encode(newPassword));

        customerRepository.save(customer);
    }

    private @NotNull Customer checkAuthentication(final String email, final String password, final String messagePasswordError) {
        final Customer customer = this.getCustomerByEmail(email);

        if (!passwordEncoder.matches(password, customer.getAuthUser().getPasswordHash())) {
            throw new IllegalArgumentException(messagePasswordError);
        }

        return customer;
    }

    private Customer getCustomerByEmail(final String email) {
        return customerRepository.findByAuthUserEmail(email)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_INVALID_EMAIL));
    }
}
