package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetCardDTO;
import bank.rest.app.bankrestapp.dto.get.GetPaymentDTO;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
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
public final class AccountMapperImpl implements Mapper<Account, GetAccountDTO> {

    private final Mapper<Card, GetCardDTO> cardMapper;
    private final Mapper<Transaction, GetTransactionDTO> transactionMapper;
    private final Mapper<Payment, GetPaymentDTO> paymentMapper;

    @Autowired
    public AccountMapperImpl(final Mapper<Card, GetCardDTO> cardMapper,
                             final Mapper<Transaction, GetTransactionDTO> transactionMapper,
                             final Mapper<Payment, GetPaymentDTO> paymentMapper) {
        this.cardMapper = cardMapper;
        this.transactionMapper = transactionMapper;
        this.paymentMapper = paymentMapper;
    }

    @Contract("_ -> new")
    @Override
    public @NotNull GetAccountDTO toDto(final @NotNull Account entity) {
        return new GetAccountDTO(
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
