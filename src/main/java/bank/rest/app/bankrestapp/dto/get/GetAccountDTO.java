package bank.rest.app.bankrestapp.dto.get;

import java.math.BigDecimal;
import java.util.List;

public record GetAccountDTO(
        Integer id,
        String accountNumber,
        BigDecimal balance,
        String currency,
        String status,
        GetCardDTO card,
        String accountType,
        String edrpou
//        List<GetTransactionDTO> transactions,
//        List<GetPaymentDTO> payments
) {}
