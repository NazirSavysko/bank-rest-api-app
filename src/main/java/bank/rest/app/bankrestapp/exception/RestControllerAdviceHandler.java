package bank.rest.app.bankrestapp.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public final class RestControllerAdviceHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public @NotNull ResponseEntity<?> handleIllegalArgumentException(@NotNull IllegalArgumentException e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public @NotNull ResponseEntity<?> handleSQLException(@NotNull SQLException e) {

        return ResponseEntity.internalServerError()
                .body("An unexpected error occurred: " + e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public @NotNull ResponseEntity<?> handleNoSuchElementException(@NotNull NoSuchElementException e) {

        return ResponseEntity
                .status(NOT_FOUND)
                .body(e.getMessage());
    }

}
