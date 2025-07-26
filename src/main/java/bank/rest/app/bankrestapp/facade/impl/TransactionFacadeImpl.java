package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import bank.rest.app.bankrestapp.service.TransactionService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class TransactionFacadeImpl implements TransactionFacade {
    private final TransactionService transactionService;
    private final DtoValidator dtoValidator;

    @Autowired
    public TransactionFacadeImpl(final TransactionService transactionService,
                                 final DtoValidator dtoValidator) {
        this.transactionService = transactionService;
        this.dtoValidator = dtoValidator;
    }

    @Override
    public void withdraw(final CreateTransaction transaction, final BindingResult bindingResult) {
        this.dtoValidator.validate(transaction, bindingResult);

        this.transactionService.withdraw(
                transaction.senderCardNumber(),
                transaction.recipientCardNumber(),
                transaction.amount(),
                transaction.description()
        );
    }
}
