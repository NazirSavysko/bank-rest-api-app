package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.*;
import bank.rest.app.bankrestapp.dto.get.AuthenticateDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import org.springframework.validation.BindingResult;

public interface CustomerFacade {

    GetCustomerDTO getCustomer(final String customerEmail);

    void register(CreateCustomerDTO createCustomerDTO, final BindingResult bindingResult);

    AuthenticateDTO authenticate(LoginDTO loginDTO, final BindingResult bindingResult);

    void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);

    void updatePassword(UpdateCustomerDTO updateCustomerDTO, BindingResult bindingResult);
}
