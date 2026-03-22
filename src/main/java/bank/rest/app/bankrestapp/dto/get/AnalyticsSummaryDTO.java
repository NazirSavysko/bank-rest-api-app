package bank.rest.app.bankrestapp.dto.get;

import java.math.BigDecimal;

public record AnalyticsSummaryDTO(
        BigDecimal totalIncoming,
        BigDecimal totalOutgoing,
        long totalTransactions,
        BigDecimal totalIbanExpenses,
        BigDecimal totalMobileExpenses,
        BigDecimal totalInternetExpenses,
        BigDecimal totalCardToCardExpenses,
        BigDecimal totalTaxExpenses,
        BigDecimal totalElectronicsExpenses
) {
    public AnalyticsSummaryDTO {
        totalIbanExpenses = totalIbanExpenses == null ? BigDecimal.ZERO : totalIbanExpenses;
        totalMobileExpenses = totalMobileExpenses == null ? BigDecimal.ZERO : totalMobileExpenses;
        totalInternetExpenses = totalInternetExpenses == null ? BigDecimal.ZERO : totalInternetExpenses;
        totalCardToCardExpenses = totalCardToCardExpenses == null ? BigDecimal.ZERO : totalCardToCardExpenses;
        totalTaxExpenses = totalTaxExpenses == null ? BigDecimal.ZERO : totalTaxExpenses;
        totalElectronicsExpenses = totalElectronicsExpenses == null ? BigDecimal.ZERO : totalElectronicsExpenses;
    }
}
