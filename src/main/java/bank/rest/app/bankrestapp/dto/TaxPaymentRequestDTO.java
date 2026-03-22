package bank.rest.app.bankrestapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaxPaymentRequestDTO {

    @NotNull(message = "Account id is required")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Tax type is required")
    private String taxType;

    @NotBlank(message = "Tax period is required")
    private String period;

    @NotBlank(message = "Receiver name is required")
    private String receiverName;
}
