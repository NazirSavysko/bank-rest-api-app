package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.MobilePaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.get.GetPaymentDTO;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public final class PaymentController {

    private final PaymentService paymentService;
    private final Mapper<Payment, GetPaymentDTO> paymentMapper;

    /**
     * Processes an IBAN payment for the authenticated customer.
     *
     * @param userDetails authenticated user details
     * @param request IBAN payment request payload
     * @return response containing the created payment DTO
     * @throws IllegalArgumentException if the request is invalid or the account does not belong to the user
     */
    @PostMapping("/iban")
    public ResponseEntity<GetPaymentDTO> processIbanPayment(
            final @AuthenticationPrincipal UserDetails userDetails,
            final @Valid @RequestBody IbanPaymentRequestDTO request
    ) {
        final Payment payment = this.paymentService.processIbanPayment(request, userDetails.getUsername());

        return ResponseEntity
                .status(CREATED)
                .body(this.paymentMapper.toDto(payment));
    }

    /**
     * Processes an internet-service payment for the authenticated customer.
     *
     * @param userDetails authenticated user details
     * @param request internet payment request payload
     * @return response containing the created payment DTO
     * @throws IllegalArgumentException if the request is invalid or the account does not belong to the user
     */
    @PostMapping("/internet")
    public ResponseEntity<GetPaymentDTO> processInternetPayment(
            final @AuthenticationPrincipal UserDetails userDetails,
            final @Valid @RequestBody InternetPaymentRequestDTO request
    ) {
        final Payment payment = this.paymentService.processInternetPayment(request, userDetails.getUsername());

        return ResponseEntity
                .status(CREATED)
                .body(this.paymentMapper.toDto(payment));
    }

    @PostMapping("/mobile")
    public ResponseEntity<String> processMobilePayment(
            final @AuthenticationPrincipal UserDetails userDetails,
            final @Valid @RequestBody MobilePaymentRequestDTO request
    ) {
        this.paymentService.processMobilePayment(request, userDetails.getUsername());
        return ResponseEntity.ok("Mobile top-up completed successfully");
    }
}
