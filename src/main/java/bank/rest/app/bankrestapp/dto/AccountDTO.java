package bank.rest.app.bankrestapp.dto;

import java.math.BigDecimal;
import java.util.List;

public record AccountDTO(
        String accountNumber,
        BigDecimal balance,
        String currency,
        String status,
        CardDTO card,
        List<TransactionDTO> transactions,
        List<PaymentDTO> payments
) {}