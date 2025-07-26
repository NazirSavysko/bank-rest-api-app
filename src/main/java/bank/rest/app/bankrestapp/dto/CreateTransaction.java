package bank.rest.app.bankrestapp.dto;

import bank.rest.app.bankrestapp.entity.annotation.CurrencyAmount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateTransaction(
        @NotBlank(message = "{transaction.sender_card_number.blank}")
        @Size(min = 16, max = 16, message = "{transaction.sender_card_number.size}")
        String senderCardNumber,
        @NotBlank(message = "{transaction.recipient_card_number.blank}")
        @Size(min = 16, max = 16, message = "{transaction.recipient_card_number.size}")
        String recipientCardNumber,
        @NotNull(message = "{transaction.amount.null}")
        @CurrencyAmount
        BigDecimal amount,
        @NotBlank(message = "{transaction.description.blank}")
        @Size(max = 255, message = "{transaction.description.size}")
        String description
) {
}
