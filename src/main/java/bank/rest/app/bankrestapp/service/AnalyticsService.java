package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;

public interface AnalyticsService {

    AnalyticsSummaryDTO getMonthlySummary(Long accountId, Integer year, Integer month, String userEmail);
}
