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

    /**
     * Returns a monthly analytics summary for the authenticated user's account.
     *
     * @param userDetails authenticated user details
     * @param accountNumber account number to summarize
     * @param year requested year
     * @param month requested month
     * @return response containing the analytics summary DTO
     * @throws IllegalArgumentException if required parameters are missing or the account is unavailable to the user
     */
    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryDTO> getSummary(final @AuthenticationPrincipal UserDetails userDetails,
                                                          final @RequestParam String accountNumber,
                                                          final @RequestParam Integer year,final @RequestParam Integer month) {
        final String userEmail = userDetails != null ? userDetails.getUsername() : null;
        final AnalyticsSummaryDTO summary = this.analyticsService.getMonthlySummary(accountNumber, year, month, userEmail);
        return ResponseEntity.ok(summary);
    }
}
