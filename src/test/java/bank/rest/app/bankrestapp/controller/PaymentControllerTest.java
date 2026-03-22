package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.MobilePaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.TaxPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.get.GetPaymentDTO;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.mapper.Mapper;
import bank.rest.app.bankrestapp.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    private PaymentService paymentService;
    private Mapper<Payment, GetPaymentDTO> paymentMapper;
    private PaymentController controller;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        this.paymentService = mock(PaymentService.class);
        this.paymentMapper = mock(Mapper.class);
        this.controller = new PaymentController(paymentService, paymentMapper);
    }

    @Test
    void processIbanPayment_ShouldUseAuthenticatedUserAndReturnCreated() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();
        final IbanPaymentRequestDTO request = new IbanPaymentRequestDTO(
                1L,
                BigDecimal.TEN,
                "Test",
                "UA123456789012345678901234567",
                "12345",
                "purpose"
        );
        final Payment payment = new bank.rest.app.bankrestapp.entity.IbanPayment();
        final GetPaymentDTO dto = new GetPaymentDTO("UAH", "10", "Test", "purpose");

        when(paymentService.processIbanPayment(request, "user@example.com")).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        final var response = controller.processIbanPayment(user, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(paymentService).processIbanPayment(request, "user@example.com");
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void processInternetPayment_ShouldUseAuthenticatedUserAndReturnCreated() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();
        final InternetPaymentRequestDTO request = new InternetPaymentRequestDTO(
                2L,
                BigDecimal.valueOf(20),
                "Lanet",
                "A-01"
        );
        final Payment payment = new bank.rest.app.bankrestapp.entity.InternetPayment();
        final GetPaymentDTO dto = new GetPaymentDTO("UAH", "20", "Lanet", "Оплата");

        when(paymentService.processInternetPayment(request, "user@example.com")).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        final var response = controller.processInternetPayment(user, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(paymentService).processInternetPayment(request, "user@example.com");
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void processMobilePayment_ShouldUseAuthenticatedUserAndReturnOk() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();
        final MobilePaymentRequestDTO request = new MobilePaymentRequestDTO(
                3L,
                BigDecimal.valueOf(30),
                "+380991112233"
        );

        final var response = controller.processMobilePayment(user, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Поповнення мобільного рахунку успішно завершено", response.getBody());
        verify(paymentService).processMobilePayment(request, "user@example.com");
        verifyNoInteractions(paymentMapper);
    }

    @Test
    void processTaxPayment_ShouldUseAuthenticatedUserAndReturnOk() {
        final UserDetails user = User.withUsername("user@example.com").password("pass").roles("USER").build();
        final TaxPaymentRequestDTO request = new TaxPaymentRequestDTO();
        request.setAccountId(4L);
        request.setAmount(BigDecimal.valueOf(45));
        request.setTaxType("Єдиний податок (5% від доходу)");
        request.setPeriod("I квартал 2026 року");
        request.setReceiverName("Держказначейство");

        final var response = controller.processTaxPayment(user, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Оплата податків успішно завершена", response.getBody());
        verify(paymentService).processTaxPayment(request, "user@example.com");
        verifyNoInteractions(paymentMapper);
    }
}
