package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.CetCustomerDetailsForAdminDTO;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static bank.rest.app.bankrestapp.utils.MapperUtils.mapCollection;

@Component
@AllArgsConstructor
public class AdminCustomerMapperImpl implements Mapper<Customer, CetCustomerDetailsForAdminDTO> {

    private AdminAccountMapperImpl accountMapper;

    @Override
    public CetCustomerDetailsForAdminDTO toDto(final @NotNull Customer entity) {
        return new CetCustomerDetailsForAdminDTO(
                entity.getFirstName() + " " + entity.getLastName(),
                entity.getAuthUser().getEmail(),
                entity.getPhone(),
                mapCollection(entity.getAccounts(),this.accountMapper::toDto)
        );
    }
}
