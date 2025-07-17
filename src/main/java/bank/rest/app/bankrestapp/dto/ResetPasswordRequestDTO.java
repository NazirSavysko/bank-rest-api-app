package bank.rest.app.bankrestapp.dto;

public record ResetPasswordRequestDTO(
        String email,
        String password
) { }
