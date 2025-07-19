package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.*;
import bank.rest.app.bankrestapp.dto.get.AuthenticateDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.facade.CustomerFacade;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.security.JwtUtil;
import bank.rest.app.bankrestapp.service.CustomerService;

import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.validation.MessageError.ERRORS_CUSTOMER_ROLE_NOT_FOUND;
import static bank.rest.app.utils.MapperUtils.mapDto;

@Component
public class CustomerFacadeImpl implements CustomerFacade {

    private final CustomerService customerService;
    private final Mapper<Customer, GetCustomerDTO> customerMapper;
    private final JwtUtil jwtUtil;
    private final DtoValidator dtoValidator;
    @Autowired
    public CustomerFacadeImpl(final CustomerService customerService,
                              final Mapper<Customer, GetCustomerDTO> customerMapper,
                                final DtoValidator dtoValidator,
                              final JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
        this.jwtUtil = jwtUtil;
        this.dtoValidator = dtoValidator;
    }

    @Override
    public GetCustomerDTO getCustomer(final String customerEmail) {
        final Customer customer = this.customerService.login(customerEmail);

        return mapDto(customer,this.customerMapper::toDto) ;
    }

    @Override
    public void register(final @NotNull CreateCustomerDTO createCustomerDTO, final BindingResult bindingResult) {
        this.dtoValidator.validate(createCustomerDTO,bindingResult);

        this.customerService.register(
                createCustomerDTO.firstName(),
                createCustomerDTO.lastName(),
                createCustomerDTO.email(),
                createCustomerDTO.password(),
                createCustomerDTO.phoneNumber()
        );
    }

    @Override
    public AuthenticateDTO authenticate(final @NotNull LoginDTO loginDTO, final BindingResult bindingResult) {
        this.dtoValidator.validate(loginDTO, bindingResult);

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

    @Override
    public void resetPassword(final @NotNull ResetPasswordRequestDTO resetPasswordRequestDTO) {
        this.customerService.resetPassword(
                resetPasswordRequestDTO.email(),
                resetPasswordRequestDTO.password()
        );
    }

    @Override
    public void updatePassword(final UpdateCustomerDTO updateCustomerDTO, final BindingResult bindingResult) {
        this.dtoValidator.validate(updateCustomerDTO,bindingResult);

        this.customerService.checkIfAuthenticated(
                updateCustomerDTO.email(),
                updateCustomerDTO.oldPassword()
        );

        this.customerService.updatePassword(
                updateCustomerDTO.email(),
                updateCustomerDTO.newPassword()
        );
    }
}
