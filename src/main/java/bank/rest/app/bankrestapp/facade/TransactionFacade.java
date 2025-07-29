package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import org.springframework.validation.BindingResult;

public interface TransactionFacade {

    GetTransactionDTO withdraw(CreateTransaction transaction, final BindingResult bindingResult);

}
