package bank.rest.app.bankrestapp.dto.get;

import bank.rest.app.bankrestapp.entity.enums.HistoryDirection;
import bank.rest.app.bankrestapp.entity.enums.HistoryItemType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryItemDTO(
        Integer id,
        BigDecimal amount,
        String currency,
        LocalDateTime createdAt,
        HistoryItemType type,
        Integer senderAccountId,
        Integer receiverAccountId,
        String details,
        HistoryDirection direction
) {}
