package bank.rest.app.bankrestapp.mapper;

import bank.rest.app.bankrestapp.dto.get.GetAccountForAdminDTO;
import bank.rest.app.bankrestapp.entity.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountMapperImpl implements Mapper<Account, GetAccountForAdminDTO> {
    @Override
    public GetAccountForAdminDTO toDto(final @NotNull Account entity) {
        return new GetAccountForAdminDTO(
                entity.getAccountId(),
                entity.getAccountNumber(),
                entity.getStatus().name()
        );
    }
}
