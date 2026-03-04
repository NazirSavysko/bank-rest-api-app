package bank.rest.app.bankrestapp.exception.advice_rest_controller;

import bank.rest.app.bankrestapp.exception.AccountNotActiveException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RestControllerAdviceHandlerTest {

    private final RestControllerAdviceHandler handler = new RestControllerAdviceHandler();

    @Test
    void handleInsufficientFundsException_ReturnsPaymentRequired() {
        ResponseEntity<?> response = handler.handleInsufficientFundsException(new InsufficientFundsException("not enough"));

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
    }

    @Test
    void handleAccountNotActiveException_ReturnsForbidden() {
        ResponseEntity<?> response = handler.handleAccountNotActiveException(new AccountNotActiveException("blocked"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
    }
}
