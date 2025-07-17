package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.CreateCustomerDTO;
import bank.rest.app.bankrestapp.dto.LoginDTO;

public interface CustomerFacade {

    Object login(LoginDTO loginDTO);

    Object register(CreateCustomerDTO createCustomerDTO);
}
