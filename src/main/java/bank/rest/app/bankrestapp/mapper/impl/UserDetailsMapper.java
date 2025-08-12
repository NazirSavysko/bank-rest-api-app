package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.CustomerRole;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
@Component
public class UserDetailsMapper implements Mapper<Customer, UserDetails> {

    @Override
    public UserDetails toDto(final Customer entity) {
        return new UserDetails() {

            @Override
            public @NotNull @Unmodifiable Collection<? extends GrantedAuthority> getAuthorities() {
                return entity.getAuthUser().getCustomerRole().stream()
                        .map(CustomerRole::getRoleName)
                        .toList();
            }

            @Override
            public String getPassword() {
                return entity.getAuthUser().getPasswordHash();
            }

            @Override
            public String getUsername() {
                return entity.getAuthUser().getEmail();
            }
        };
    }
}
