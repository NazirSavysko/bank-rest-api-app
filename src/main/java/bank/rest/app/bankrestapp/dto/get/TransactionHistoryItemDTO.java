package bank.rest.app.bankrestapp.dto.get;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryItemDTO(
        Long operationId,
        TransactionHistoryType type,
        TransactionHistoryDirection direction,
        BigDecimal amount,
        String currencyCode,
        String status,
        String description,
        LocalDateTime createdAt,
        String recipientIban,
        String recipientName,
        String providerName,
        String contractNumber
) {
}
