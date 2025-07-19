package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.AccountDTO;
import bank.rest.app.bankrestapp.dto.CardDTO;
import bank.rest.app.bankrestapp.dto.PaymentDTO;
import bank.rest.app.bankrestapp.dto.TransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static bank.rest.app.utils.MapperUtils.mapCollection;
import static bank.rest.app.utils.MapperUtils.mapDto;

@Component
public final class AccountMapperImpl implements Mapper<Account, AccountDTO> {

    private final Mapper<Card, CardDTO> cardMapper;
    private final Mapper<Transaction, TransactionDTO> transactionMapper;
    private final Mapper<Payment, PaymentDTO> paymentMapper;

    @Autowired
    public AccountMapperImpl(final Mapper<Card, CardDTO> cardMapper,
                             final Mapper<Transaction, TransactionDTO> transactionMapper,
                             final Mapper<Payment, PaymentDTO> paymentMapper) {
        this.cardMapper = cardMapper;
        this.transactionMapper = transactionMapper;
        this.paymentMapper = paymentMapper;
    }

    @Contract("_ -> new")
    @Override
    public @NotNull AccountDTO toDto(final @NotNull Account entity) {
        return new AccountDTO(
                entity.getAccountNumber(),
                entity.getBalance(),
                entity.getCurrencyCode().name(),
                entity.getStatus().name(),
                mapDto(entity.getCard(),this.cardMapper::toDto),
                mapCollection(entity.getTransactionHistory(), this.transactionMapper::toDto),
                mapCollection(entity.getPaymentsList(), this.paymentMapper::toDto)
        ) ;
    }
}
