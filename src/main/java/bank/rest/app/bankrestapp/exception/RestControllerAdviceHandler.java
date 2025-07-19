package bank.rest.app.bankrestapp.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static java.util.Map.of;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestControllerAdvice
public final class RestControllerAdviceHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public @NotNull ResponseEntity<?> handleIllegalArgumentException(@NotNull IllegalArgumentException e) {

        return ResponseEntity.badRequest()
                .contentType(APPLICATION_JSON)
                .body(
                        of(
                                "error", "Bad Request",
                                "message", e.getMessage()
                        )
                );
    }

    @ExceptionHandler(SQLException.class)
    public @NotNull ResponseEntity<?> handleSQLException(@NotNull SQLException e) {

        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .contentType(APPLICATION_JSON)
                .body(of(
                        "error", "Database error",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public @NotNull ResponseEntity<?> handleNoSuchElementException(@NotNull NoSuchElementException e) {

        return ResponseEntity
                .status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(
                        of(
                                "error", "Not Found",
                                "message", e.getMessage()
                        )
                );
    }

}
