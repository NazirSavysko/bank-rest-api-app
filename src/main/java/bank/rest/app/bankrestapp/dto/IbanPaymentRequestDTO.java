package bank.rest.app.bankrestapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record IbanPaymentRequestDTO(
        @NotNull(message = "Account id is required")
        Long accountId,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        @NotBlank(message = "Recipient name is required")
        String recipientName,
        @NotBlank(message = "Recipient IBAN is required")
        @Pattern(
                regexp = "^UA[A-Z0-9]{32}$",
                message = "Recipient IBAN must start with UA and contain 29 characters"
        )
        String recipientIban,
        @NotBlank(message = "Tax number is required")
        String taxNumber,
        @NotBlank(message = "Purpose is required")
        String purpose
) {
}
