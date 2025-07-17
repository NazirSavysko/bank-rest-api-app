package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.AuthUSer;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.CustomerRole;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.resository.CustomerRoleRepository;
import bank.rest.app.bankrestapp.service.CustomerService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.entity.enums.Role.USER_ROLE;
import static java.time.LocalDateTime.now;
import static java.util.List.of;

@Service
public final class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomerRoleRepository customerRoleRepository;

    @Autowired
    public CustomerServiceImpl(final CustomerRepository customerRepository,
                               final BCryptPasswordEncoder bCryptPasswordEncoder,
                               final CustomerRoleRepository customerRoleRepository) {
        this.customerRepository = customerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customerRoleRepository = customerRoleRepository;
    }

    @Override
    public @NotNull Customer login(final String email, final String password) {
        final Customer customer = customerRepository.findByAuthUserEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with email: " + email));

        if (!bCryptPasswordEncoder.matches(password, customer.getAuthUser().getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return customer;
    }

    @Override
    public @NotNull Customer register(final String firstName,
                                      final String lastName,
                                      final String email,
                                      final String password,
                                      final String phoneNumber) {
        if (customerRepository.findByAuthUserEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        final CustomerRole customerRole = this.customerRoleRepository.findByRoleName(USER_ROLE).
                orElseThrow(() -> new NoSuchElementException("Customer role not found"));

        final AuthUSer authUser = AuthUSer.builder()
                .email(email)
                .passwordHash(bCryptPasswordEncoder.encode(password))
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

        return this.customerRepository.save(customer);
    }
}
