package bank.rest.app.bankrestapp.exception.advice_rest_controller;

import bank.rest.app.bankrestapp.exception.AccountNotActiveException;
import bank.rest.app.bankrestapp.exception.InvalidAccountCurrencyException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import bank.rest.app.bankrestapp.exception.RecipientNotFoundException;
import bank.rest.app.bankrestapp.exception.UnsupportedCurrencyException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_OPERATION_TEMPORARILY_UNAVAILABLE;
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
                        "error", "Некоректний запит",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(SQLException.class)
    public @NotNull ResponseEntity<?> handleSQLException(@NotNull SQLException e) {
        return ResponseEntity.internalServerError()
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Помилка бази даних",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public @NotNull ResponseEntity<?> handleNoSuchElementException(@NotNull NoSuchElementException e) {
        return ResponseEntity.status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Не знайдено",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(UncheckedIOException.class)
    public @NotNull ResponseEntity<?> handleTemplateReadException(@NotNull UncheckedIOException e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Помилка шаблону електронного листа",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public @NotNull ResponseEntity<?> handleEmailSendError(@NotNull ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Помилка надсилання електронного листа",
                        "message", e.getMessage()
                ));
    }


    @ExceptionHandler(AccountNotActiveException.class)
    public @NotNull ResponseEntity<?> handleAccountNotActiveException(@NotNull AccountNotActiveException e) {
        return ResponseEntity.status(LOCKED)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Рахунок не активний",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public @NotNull ResponseEntity<?> handleInsufficientFundsException(@NotNull InsufficientFundsException e) {

        return ResponseEntity.status(BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Недостатньо коштів",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler({CannotAcquireLockException.class, PessimisticLockingFailureException.class})
    public @NotNull ResponseEntity<?> handlePessimisticLockingFailureException(@NotNull RuntimeException e) {
        return ResponseEntity.status(SERVICE_UNAVAILABLE)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Конфлікт блокування",
                        "message", ERRORS_OPERATION_TEMPORARILY_UNAVAILABLE
                ));
    }

    @ExceptionHandler(InvalidAccountCurrencyException.class)
    public @NotNull ResponseEntity<?> handleInvalidAccountCurrencyException(@NotNull InvalidAccountCurrencyException e) {

        return ResponseEntity.status(UNPROCESSABLE_ENTITY)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Некоректна валюта рахунку",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(UnsupportedCurrencyException.class)
    public @NotNull ResponseEntity<?> handleUnsupportedCurrencyException(@NotNull UnsupportedCurrencyException e) {

        return ResponseEntity.status(UNPROCESSABLE_ENTITY)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Непідтримувана валюта",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(RecipientNotFoundException.class)
    public @NotNull ResponseEntity<?> handleRecipientNotFoundException(@NotNull RecipientNotFoundException e) {

        return ResponseEntity.status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(of(
                        "timestamp", now(),
                        "error", "Отримувача не знайдено",
                        "message", e.getMessage()
                ));
    }
}
