package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.TransactionService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import static bank.rest.app.bankrestapp.utils.MapperUtils.mapDto;

@Component
@AllArgsConstructor
public class TransactionFacadeImpl implements TransactionFacade {
    private final TransactionService transactionService;
    private final DtoValidator dtoValidator;
    private final Mapper<Transaction, GetTransactionDTO> transactionMapper;
    private final CurrencyLoader currencyLoader;
    private final AccountService accountService;

    @Override
    public GetTransactionDTO withdraw(final CreateTransaction transaction, final BindingResult bindingResult) {
        this.dtoValidator.validate(transaction, bindingResult);

        final Transaction getTransaction = this.transactionService.withdraw(
                transaction.senderCardNumber(),
                transaction.recipientCardNumber(),
                transaction.amount(),
                transaction.description()
        );
        return mapDto(getTransaction, this.transactionMapper::toDto);
    }

    @Override
    public Page<GetTransactionDTO> getTransactionHistory(final String accountNumber, final int page, final int size) {
        final Account account = this.accountService.getAccountByNumber(accountNumber);
        Page<Transaction> transactionPage = this.transactionService.getTransactionHistory(accountNumber, page, size);

        return transactionPage.map(transaction -> {
            transaction.setAmount(currencyLoader.convert(transaction.getAmount(), transaction.getCurrencyCode().name(), account.getCurrencyCode().name()));
            transaction.setCurrencyCode(account.getCurrencyCode());

            if (Boolean.TRUE.equals(transaction.getIsRecipient()) || transaction.getToAccount().equals(account)) {
                transaction.setIsRecipient(Boolean.TRUE);
            }
            return transactionMapper.toDto(transaction);
        });
    }
}
