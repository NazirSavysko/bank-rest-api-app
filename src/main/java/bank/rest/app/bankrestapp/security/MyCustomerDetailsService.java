package bank.rest.app.bankrestapp.security;

import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public final class MyCustomerDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final Mapper<Customer, UserDetails> userDetailsMapper;


    @Override
    public @NotNull UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Customer customer = customerRepository.findByAuthUserEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Customer not found with email: " + username)
        );

        return this.userDetailsMapper.toDto(customer);
    }
}
