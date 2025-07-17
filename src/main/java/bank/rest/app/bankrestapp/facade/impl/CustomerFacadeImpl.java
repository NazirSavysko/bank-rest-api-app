package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.CreateCustomerDTO;
import bank.rest.app.bankrestapp.dto.LoginDTO;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import bank.rest.app.bankrestapp.service.CustomerService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class CustomerFacadeImpl implements CustomerFacade {

    private final CustomerService customerService;

    @Autowired
    public CustomerFacadeImpl(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @Contract("_ -> new")
    @Override
    public @NotNull Object login(final @NotNull LoginDTO loginDTO) {

        final Customer customer = this.customerService.login(loginDTO.email(), loginDTO.password());

        return new Object();
    }

    @Contract(value = "_ -> new", pure = true)
    @Override
    public @NotNull Object register(final @NotNull CreateCustomerDTO createCustomerDTO) {

        final Customer customer = this.customerService.register(
                createCustomerDTO.firstName(),
                createCustomerDTO.lastName(),
                createCustomerDTO.email(),
                createCustomerDTO.password(),
                createCustomerDTO.phoneNumber()
        );

        return new Object();
    }
}
