package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.CreateTransaction;

public interface TransactionFacade {

    void withdraw(CreateTransaction transaction);

}
