package bank.rest.app.bankrestapp.dto.get;

public record GetAccountForAdminDTO(
        Integer id,
        String accountNumber,
        String accountType
) {
}
