package bank.rest.app.bankrestapp.dto;

public record VerifyCodeDTO(
        String email,
        String code
) { }
