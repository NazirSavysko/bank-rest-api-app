package bank.rest.app.bankrestapp.dto;

import java.math.BigDecimal;

public record TransactionDTO(
        ShortCustomerDTO sender,
        ShortCustomerDTO receiver,
        BigDecimal amount,
        String description,
        String transactionDate,
        String transactionType,
        String currencyCode,
        String status
) {
}
