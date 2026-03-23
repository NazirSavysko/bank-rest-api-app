package bank.rest.app.bankrestapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequestDTO(
        @NotBlank(message = "{user.code.blank}")
        String verificationCode,
        @NotBlank(message = "{user.email.blank}")
        @Email(message = "{user.email.invalid}")
        String newEmail
) {
}
