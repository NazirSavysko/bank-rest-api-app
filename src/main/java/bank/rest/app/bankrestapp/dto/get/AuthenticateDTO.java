package bank.rest.app.bankrestapp.dto.get;

public record AuthenticateDTO(
        String token,
        String role
) {
}
