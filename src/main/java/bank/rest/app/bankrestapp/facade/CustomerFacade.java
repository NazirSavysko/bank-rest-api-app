package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.*;

public interface CustomerFacade {

    CustomerDTO getCustomer(final String customerEmail);

    void register(CreateCustomerDTO createCustomerDTO);

    AuthenticateDTO authenticate(LoginDTO loginDTO);
}
