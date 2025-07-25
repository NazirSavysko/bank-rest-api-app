package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import bank.rest.app.bankrestapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionFacadeImpl implements TransactionFacade {
    private final TransactionService transactionService;

    @Autowired
    public TransactionFacadeImpl(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @Override
    public void withdraw(final CreateTransaction transaction) {

    }
}
