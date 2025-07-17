package bank.rest.app.bankrestapp.security;

import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public final class MyCustomerDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Autowired
    public MyCustomerDetailsService(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public @NotNull UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Customer customer = customerRepository.findByAuthUserEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Customer not found with email: " + username)
        );

        return new CustomerPrincipal(customer);
    }
}
