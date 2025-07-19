package bank.rest.app.bankrestapp.dto.get;

import java.math.BigDecimal;

public record GetTransactionDTO(
        GetShortCustomerDTO sender,
        GetShortCustomerDTO receiver,
        BigDecimal amount,
        String description,
        String transactionDate,
        String transactionType,
        String currencyCode,
        String status
) {
}
