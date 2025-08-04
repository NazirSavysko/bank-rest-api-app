package bank.rest.app.bankrestapp.dto;

import bank.rest.app.bankrestapp.entity.annotation.AccountStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountStatusDTO(
        @NotNull(message = "{account.id.null}")
        Integer id,
        @NotBlank(message = "{account.status.blank}")
        @AccountStatus
        String status
) {
}
