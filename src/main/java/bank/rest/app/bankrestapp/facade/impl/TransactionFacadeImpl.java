package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.service.TransactionService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import static bank.rest.app.bankrestapp.utils.MapperUtils.mapDto;

@Component
@AllArgsConstructor
public class TransactionFacadeImpl implements TransactionFacade {
    private final TransactionService transactionService;
    private final DtoValidator dtoValidator;
    private final Mapper<Transaction, GetTransactionDTO> transactionMapper;

    @Override
    public GetTransactionDTO withdraw(final CreateTransaction transaction, final BindingResult bindingResult) {
        this.dtoValidator.validate(transaction, bindingResult);

     final Transaction getTransaction = this.transactionService.withdraw(
                transaction.senderCardNumber(),
                transaction.recipientCardNumber(),
                transaction.amount(),
                transaction.description()
        );
        return mapDto(getTransaction,this.transactionMapper::toDto);
    }
}
