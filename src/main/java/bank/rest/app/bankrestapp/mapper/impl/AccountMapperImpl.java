package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetCardDTO;
import bank.rest.app.bankrestapp.dto.get.GetPaymentDTO;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.*;
import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.CANCELLED;
import static bank.rest.app.bankrestapp.utils.MapperUtils.mapCollection;
import static bank.rest.app.bankrestapp.utils.MapperUtils.mapDto;
import static java.util.stream.Stream.concat;

@Component
@AllArgsConstructor
public final class AccountMapperImpl implements Mapper<Account, GetAccountDTO> {

    private final Mapper<Card, GetCardDTO> cardMapper;
    private final Mapper<Transaction, GetTransactionDTO> transactionMapper;
    private final Mapper<Payment, GetPaymentDTO> paymentMapper;
    private final CurrencyLoader currencyLoader;


    private static boolean test(@NotNull Transaction transaction) {
        return !(transaction.getStatus().equals(CANCELLED) || transaction.getStatus().equals(FAILED));
    }

    @Contract("_ -> new")
    @Override
    public @NotNull GetAccountDTO toDto(final @NotNull Account entity) {
        return new GetAccountDTO(
                entity.getAccountNumber(),
                entity.getBalance(),
                entity.getCurrencyCode().name(),
                entity.getStatus().name(),
                mapDto(entity.getCard(), this.cardMapper::toDto),
                mapCollection(this.getTransactionHistory(entity.getSentTransactions(), entity.getReceivedTransactions(), entity), this.transactionMapper::toDto),
                mapCollection(entity.getPaymentsList(), this.paymentMapper::toDto)
        );
    }

    private @NotNull List<Transaction> getTransactionHistory(final @NotNull List<Transaction> senderTransactions, final @NotNull List<Transaction> recipientTransactions, final Account account) {

        if (senderTransactions.isEmpty() && recipientTransactions.isEmpty()) {
            return List.of();
        }

        if (recipientTransactions.isEmpty()) {
            return senderTransactions;
        }

        final Stream<Transaction> getStream = this.getTransactionHistory(recipientTransactions, account);
        if (senderTransactions.isEmpty()) {
            return getStream.toList();
        }

        return concat(getStream, senderTransactions.stream()).toList();

    }

    private Stream<Transaction> getTransactionHistory(final @NotNull List<Transaction> recipientTransactions, final Account account) {
        return recipientTransactions.stream()
                .filter(AccountMapperImpl::test)
                .peek(transaction -> {
                    transaction.setAmount(currencyLoader.convert(transaction.getAmount(), transaction.getCurrencyCode().name(), account.getCurrencyCode().name()));
                    transaction.setCurrencyCode(account.getCurrencyCode());
                    transaction.setIsRecipient(true);
                });
    }
}
