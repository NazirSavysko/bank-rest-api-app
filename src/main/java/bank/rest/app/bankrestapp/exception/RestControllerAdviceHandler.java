package bank.rest.app.bankrestapp.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestControllerAdvice
public final class RestControllerAdviceHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public @NotNull ResponseEntity<?> handleIllegalArgumentException(@NotNull IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Bad Request",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(SQLException.class)
    public @NotNull ResponseEntity<?> handleSQLException(@NotNull SQLException e) {
        return ResponseEntity.internalServerError()
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Database Error",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public @NotNull ResponseEntity<?> handleNoSuchElementException(@NotNull NoSuchElementException e) {
        return ResponseEntity.status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Not Found",
                        "message", e.getMessage()
                ));
    }

    // ===== Email HTML шаблон ошибка =====
    @ExceptionHandler(UncheckedIOException.class)
    public @NotNull ResponseEntity<?> handleTemplateReadException(@NotNull UncheckedIOException e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Email Template Error",
                        "message", e.getMessage()
                ));
    }

    // ===== Ошибка отправки email =====
    @ExceptionHandler(ResponseStatusException.class)
    public @NotNull ResponseEntity<?> handleEmailSendError(@NotNull ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Email Sending Error",
                        "message", e.getMessage()
                ));
    }


    @ExceptionHandler(AccountNotActiveException.class)
    public @NotNull ResponseEntity<?> handleAccountNotActiveException(@NotNull AccountNotActiveException e) {
        return ResponseEntity.status(LOCKED)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Account Not Active",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public @NotNull ResponseEntity<?> handleInsufficientFundsException(@NotNull InsufficientFundsException e) {

        return ResponseEntity.status(BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Insufficient Funds",
                        "message", e.getMessage()
                ));
    }
}
