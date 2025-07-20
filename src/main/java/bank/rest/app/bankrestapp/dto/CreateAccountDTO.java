package bank.rest.app.bankrestapp.dto;

import bank.rest.app.bankrestapp.entity.annotation.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAccountDTO(
        @NotBlank(message = "{account.account_type.blank}")
        @Size(min = 3, max = 3, message = "{account.account_type.size}")
        @Currency
        String accountType,
        @NotBlank(message = "{user.email.blank}")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "{user.email.invalid}"
        )
        @Size(max = 150, message = "{user.email.too_long}")
        String customerEmail
) {
}
