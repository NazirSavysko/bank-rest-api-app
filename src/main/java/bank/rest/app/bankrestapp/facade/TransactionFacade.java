package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import org.springframework.validation.BindingResult;

public interface TransactionFacade {

    void withdraw(CreateTransaction transaction, final BindingResult bindingResult);

}
