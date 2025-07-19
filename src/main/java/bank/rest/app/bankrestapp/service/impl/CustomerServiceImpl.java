package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.AuthUSer;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.CustomerRole;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.resository.CustomerRoleRepository;
import bank.rest.app.bankrestapp.service.CustomerService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.entity.enums.Role.ROLE_USER;
import static bank.rest.app.bankrestapp.validation.Error.*;
import static java.time.LocalDateTime.now;
import static java.util.List.of;

@Service
public  class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRoleRepository customerRoleRepository;

    @Autowired
    public CustomerServiceImpl(final CustomerRepository customerRepository,
                               final  PasswordEncoder passwordEncoder,
                               final CustomerRoleRepository customerRoleRepository) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRoleRepository = customerRoleRepository;
    }

    @Override
    public @NotNull Customer login(final String email) {

        return customerRepository.findByAuthUserEmail(email)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_INVALID_EMAIL));
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

        final CustomerRole customerRole = this.customerRoleRepository.findByRoleName(ROLE_USER).orElseThrow(
                () -> new NoSuchElementException(ERRORS_CUSTOMER_ROLE_NOT_FOUND)
        );

        final AuthUSer authUser = AuthUSer.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .customerRole(of(customerRole))
                .createdAt(now())
                .build();

        final Customer customer = Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phoneNumber)
                .authUser(authUser)
                .createdAt(now())
                .build();

        this.customerRepository.save(customer);
    }

    @Override
    public Customer checkIfAuthenticated(final String email, final String password) {
        final Customer customer = customerRepository.findByAuthUserEmail(email)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_INVALID_EMAIL));

        if (!passwordEncoder.matches(password, customer.getAuthUser().getPasswordHash())) {
            throw new IllegalArgumentException(ERRORS_INVALID_PASSWORD);
        }

        return customer;
    }
}
