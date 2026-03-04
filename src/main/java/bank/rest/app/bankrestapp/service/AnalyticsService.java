package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;

public interface AnalyticsService {
    AnalyticsSummaryDTO getMonthlySummary(String accountNumber, Integer year, Integer month, String userEmail);
}
