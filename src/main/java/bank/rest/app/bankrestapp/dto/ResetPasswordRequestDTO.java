package bank.rest.app.bankrestapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "{user.email.blank}")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "{user.email.invalid}"
        )
        @Size(max = 150, message = "{user.email.too_long}")
        String email,

        @NotBlank(message = "{user.password.blank}")
        @Size(min = 8, max = 15, message = "{user.password.size}")
        String password
) {
}
