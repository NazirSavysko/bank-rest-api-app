package bank.rest.app.bankrestapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerDTO(

        @NotBlank(message = "{user.first_name.blank}")
        @Size(min = 4, max = 100, message = "{user.first_name.size}")
        String firstName,

        @NotBlank(message = "{user.last_name.blank}")
        @Size(min = 4, max = 100, message = "{user.last_name.size}")
        String lastName,

        @NotBlank(message = "{user.email.blank}")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "{user.email.invalid}"
        )
        @Size(max = 150, message = "{user.email.too_long}")
        String email,

        @NotBlank(message = "{user.password.blank}")
        @Size(min = 8, max = 15, message = "{user.password.size}")
        String password,

        @NotBlank(message = "{user.phone.blank}")
        @Pattern(
                regexp = "^\\+380\\d{9}$",
                message = "{user.phone.invalid}"
        )
        String phoneNumber
) {
}

