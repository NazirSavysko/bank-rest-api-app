package bank.rest.app.bankrestapp.dto.get;

import java.math.BigDecimal;

public record AnalyticsSummaryDTO(
        Long accountId,
        Integer year,
        Integer month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        Long operationsCount,
        String currency
) { }
