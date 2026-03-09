package bank.rest.app.bankrestapp.dto.get;

import java.math.BigDecimal;

public record AnalyticsSummaryDTO(
        BigDecimal totalIncoming,
        BigDecimal totalOutgoing,
        long totalTransactions
) { }
