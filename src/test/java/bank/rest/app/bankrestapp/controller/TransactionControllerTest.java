package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import bank.rest.app.bankrestapp.idempotency.WithdrawIdempotencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionFacade transactionFacade;
    @Mock
    private WithdrawIdempotencyService withdrawIdempotencyService;

    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        this.transactionController = new TransactionController(transactionFacade, withdrawIdempotencyService);
    }

    @Test
    void withdraw_WhenBindingResultHasErrors_ReturnsBadRequestWithStructuredMessages() {
        CreateTransaction request = new CreateTransaction("1", "2", BigDecimal.ONE, "d");
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "transaction");
        bindingResult.rejectValue("senderCardNumber", "size", "size error");

        ResponseEntity<?> response = transactionController.withdraw(request, bindingResult, "key-1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Validation Failed", body.get("error"));
        assertInstanceOf(List.class, body.get("messages"));
    }

    @Test
    void withdraw_WhenSuccess_CachesIdempotencyResponse() {
        CreateTransaction request = new CreateTransaction("1111222233334444", "5555666677778888", BigDecimal.TEN, "Payment");
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "transaction");
        GetTransactionDTO dto = new GetTransactionDTO(null, null, BigDecimal.TEN, "Payment", "2026-01-01T10:00:00", "TRANSFER", "USD", "COMPLETED", "1111222233334444", false, "5555666677778888");
        when(transactionFacade.withdraw(request, bindingResult)).thenReturn(dto);

        ResponseEntity<?> response = transactionController.withdraw(request, bindingResult, "idem-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(withdrawIdempotencyService).cacheSuccessfulResponse("idem-123", dto);
    }
}
