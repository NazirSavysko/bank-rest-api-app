package bank.rest.app.bankrestapp.dto.get;

import java.time.LocalDateTime;

public record GetCardDTO(
        String cardNumber,
        LocalDateTime expirationDate,
        String cvv
) {
}
