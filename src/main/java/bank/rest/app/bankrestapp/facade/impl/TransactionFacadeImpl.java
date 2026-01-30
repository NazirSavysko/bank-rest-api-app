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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.CANCELLED;
import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.FAILED;
import static bank.rest.app.bankrestapp.utils.MapperUtils.mapDto;
import static java.util.stream.Stream.concat;

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
    public Page<GetTransactionDTO> getAllTransactions(final Pageable pageable, final String accountNumber) {
        final Account account = this.accountService.getAccountByNumber(accountNumber);
        final List<GetTransactionDTO> transactions = getTransactionHistory(this.transactionService.getAllTransactions(accountNumber),account);

        return new PageImpl<>(transactions, pageable, transactions.size());
    }


    private List<GetTransactionDTO> getTransactionHistory(final @NotNull List<Transaction> transactions, final Account account) {
        return transactions.stream()
                .filter(transaction -> test(transaction, account))
                .peek(transaction -> {
                    transaction.setAmount(currencyLoader.convert(transaction.getAmount(), transaction.getCurrencyCode().name(), account.getCurrencyCode().name()));
                    transaction.setCurrencyCode(account.getCurrencyCode());
                }).map(transactionMapper::toDto)
                .toList();
    }

    private static boolean test(@NotNull Transaction transaction,final Account account) {
        return !(transaction.getStatus().equals(CANCELLED) || transaction.getStatus().equals(FAILED) || transaction.getToAccount().equals(account));
    }
}
