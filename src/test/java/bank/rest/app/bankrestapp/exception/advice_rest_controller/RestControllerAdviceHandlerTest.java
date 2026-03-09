package bank.rest.app.bankrestapp.exception.advice_rest_controller;

import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_OPERATION_TEMPORARILY_UNAVAILABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RestControllerAdviceHandlerTest {

    private final RestControllerAdviceHandler handler = new RestControllerAdviceHandler();

    @Test
    void handlePessimisticLockingFailureException_ShouldReturnLocalizedMessage() {
        final var response = handler.handlePessimisticLockingFailureException(
                new CannotAcquireLockException("lock timeout")
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());

        @SuppressWarnings("unchecked")
        final Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Конфлікт блокування", body.get("error"));
        assertEquals(ERRORS_OPERATION_TEMPORARILY_UNAVAILABLE, body.get("message"));
    }
}
