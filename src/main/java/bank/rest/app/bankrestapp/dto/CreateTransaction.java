package bank.rest.app.bankrestapp.dto;

import java.math.BigDecimal;

public record CreateTransaction(
        String senderCardNumber,
        String recipientCardNumber,
        BigDecimal amount
) {
}
