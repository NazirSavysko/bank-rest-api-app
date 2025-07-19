package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetShortCustomerDTO;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static bank.rest.app.utils.MapperUtils.mapDto;

@Component
public final class TransationMapperImpl implements Mapper<Transaction, GetTransactionDTO> {

    @Contract("_ -> new")
    @Override
    public @NotNull GetTransactionDTO toDto(final Transaction entity) {
        final Mapper<Customer, GetShortCustomerDTO> customerMapper =
                (customer) -> new GetShortCustomerDTO(
                        customer.getFirstName(),
                        customer.getLastName()
                );

        return new GetTransactionDTO(
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
