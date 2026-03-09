package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;

public interface AnalyticsService {
    /**
     * Builds a monthly summary of incoming and outgoing operations for the specified account.
     *
     * @param accountNumber account number to analyze
     * @param year calendar year of the requested period
     * @param month calendar month of the requested period
     * @param userEmail email of the authenticated user requesting the summary
     * @return aggregated analytics summary for the requested month
     * @throws IllegalArgumentException if required parameters are missing or the account does not belong to the user
     */
    AnalyticsSummaryDTO getMonthlySummary(String accountNumber, Integer year, Integer month, String userEmail);
}
