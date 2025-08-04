package bank.rest.app.bankrestapp.dto.get;

import java.util.List;

public record CetCustomerDetailsForAdminDTO(
        String fullName,
        String email,
        String phoneNumber,
        List<GetAccountForAdminDTO> accountsDTO
) {
}
