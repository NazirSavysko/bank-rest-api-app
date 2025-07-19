package bank.rest.app.bankrestapp.dto;

import java.time.LocalDateTime;

public record CardDTO(
        String cardNumber,
        LocalDateTime expirationDate,
        String cvv
) {
}
