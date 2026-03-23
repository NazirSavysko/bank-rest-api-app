package bank.rest.app.bankrestapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TravelPaymentRequestDTO {

    @NotNull(message = "Account id is required")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Transport type is required")
    private String transportType;

    @NotBlank(message = "Route is required")
    private String route;

    @NotBlank(message = "Ticket details are required")
    private String ticketDetails;
}
