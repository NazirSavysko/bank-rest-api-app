package bank.rest.app.bankrestapp.security;

import bank.rest.app.bankrestapp.entity.Customer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public final class CustomerPrincipal implements UserDetails {

    private final Customer customer;


    public CustomerPrincipal(Customer customer) {
        this.customer = customer;
    }

    @Override
    public @NotNull @Unmodifiable Collection<? extends GrantedAuthority> getAuthorities() {
         return customer.getAuthUser().getCustomerRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return customer.getAuthUser().getPasswordHash();
    }

    @Override
    public String getUsername() {
        return customer.getAuthUser().getEmail();
    }
}
