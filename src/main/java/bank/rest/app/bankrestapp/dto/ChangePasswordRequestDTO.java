package bank.rest.app.bankrestapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(
        @NotBlank(message = "{user.code.blank}")
        String verificationCode,
        @NotBlank(message = "{user.new_password.blank}")
        @Size(min = 8, message = "{user.new_password.size}")
        String newPassword
) {
}
