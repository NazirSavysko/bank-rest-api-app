package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.get.AnalyticsSummaryDTO;
import bank.rest.app.bankrestapp.service.AnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@AllArgsConstructor
public final class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryDTO> getSummary(final @AuthenticationPrincipal UserDetails userDetails,
                                                          final @RequestParam String accountNumber,
                                                          final @RequestParam Integer year,final @RequestParam Integer month) {
        final String userEmail = userDetails != null ? userDetails.getUsername() : null;
        final AnalyticsSummaryDTO summary = this.analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);
        return ResponseEntity.ok(summary);
    }
}
