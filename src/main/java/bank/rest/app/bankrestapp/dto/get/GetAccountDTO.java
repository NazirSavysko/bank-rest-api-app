package bank.rest.app.bankrestapp.dto.get;

import java.math.BigDecimal;
import java.util.List;

public record GetAccountDTO(
        String accountNumber,
        BigDecimal balance,
        String currency,
        String status,
        GetCardDTO card,
        List<GetTransactionDTO> transactions,
        List<GetPaymentDTO> payments
) {}