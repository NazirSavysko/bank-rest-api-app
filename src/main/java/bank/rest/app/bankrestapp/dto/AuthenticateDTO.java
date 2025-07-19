package bank.rest.app.bankrestapp.dto;

public record AuthenticateDTO(
        String token,
        String role
) {
}
