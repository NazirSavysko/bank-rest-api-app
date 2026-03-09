package bank.rest.app.bankrestapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record InternetPaymentRequestDTO(
        @NotNull(message = "Account id is required")
        Long accountId,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        @NotBlank(message = "Provider name is required")
        String providerName,
        @NotBlank(message = "Contract number is required")
        String contractNumber
) {
}
