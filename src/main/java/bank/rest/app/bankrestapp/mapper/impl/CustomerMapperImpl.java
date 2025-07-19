package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static bank.rest.app.utils.MapperUtils.mapCollection;

@Component
public final class CustomerMapperImpl implements Mapper<Customer, GetCustomerDTO> {

    private final Mapper<Account, GetAccountDTO> accountMapper;

    @Autowired
    public CustomerMapperImpl(final Mapper<Account, GetAccountDTO> accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Contract("_ -> new")
    @Override
    public @NotNull GetCustomerDTO toDto(final @NotNull Customer entity) {
        return new GetCustomerDTO(
                mapCollection(entity.getAccounts(), this.accountMapper::toDto),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAuthUser().getEmail(),
                entity.getPhone()
        );
    }
}
