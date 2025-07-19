package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.ShortCustomerDTO;
import bank.rest.app.bankrestapp.dto.TransactionDTO;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static bank.rest.app.utils.MapperUtils.mapDto;

@Component
public final class TransationMapperImpl implements Mapper<Transaction, TransactionDTO> {

    @Contract("_ -> new")
    @Override
    public @NotNull TransactionDTO toDto(final Transaction entity) {
        final Mapper<Customer, ShortCustomerDTO> customerMapper =
                (customer) -> new ShortCustomerDTO(
                        customer.getFirstName(),
                        customer.getLastName()
                );

        return new TransactionDTO(
                mapDto(entity.getAccount().getCustomer(), customerMapper::toDto),
                mapDto(entity.getToAccount().getCustomer(), customerMapper::toDto),
                entity.getAmount(),
                entity.getDescription(),
                entity.getTransactionDate().toString(),
                entity.getTransactionType().name(),
                entity.getCurrencyCode().name(),
                entity.getStatus().name()
        );
    }
}
