package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.*;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.security.JwtUtil;
import bank.rest.app.bankrestapp.service.CustomerService;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.validation.Error.ERRORS_CUSTOMER_ROLE_NOT_FOUND;
import static bank.rest.app.utils.MapperUtils.mapDto;

@Component
public class CustomerFacadeImpl implements CustomerFacade {

    private final CustomerService customerService;
    private final Mapper<Customer, CustomerDTO> customerMapper;
    private final JwtUtil jwtUtil;
    @Autowired
    public CustomerFacadeImpl(final CustomerService customerService,
                              final Mapper<Customer, CustomerDTO> customerMapper,
                              final JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public CustomerDTO getCustomer(final String customerEmail) {
        final Customer customer = this.customerService.login(customerEmail);

        return mapDto(customer,this.customerMapper::toDto) ;
    }

    @Override
    public void register(final @NotNull CreateCustomerDTO createCustomerDTO) {
        this.customerService.register(
                createCustomerDTO.firstName(),
                createCustomerDTO.lastName(),
                createCustomerDTO.email(),
                createCustomerDTO.password(),
                createCustomerDTO.phoneNumber()
        );
    }

    @Override
    public AuthenticateDTO authenticate(final @NotNull LoginDTO loginDTO) {
        final Customer customer = this.customerService.checkIfAuthenticated(
                loginDTO.email(),
                loginDTO.password()
        );

        final String customerRole = customer.getAuthUser().getCustomerRole()
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(ERRORS_CUSTOMER_ROLE_NOT_FOUND))
                .getRoleName()
                .name();

        final String token = jwtUtil.generateToken(customer);

        return new AuthenticateDTO(token, customerRole);
    }
}
