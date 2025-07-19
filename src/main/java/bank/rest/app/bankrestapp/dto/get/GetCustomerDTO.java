package bank.rest.app.bankrestapp.dto.get;

import java.util.List;

public record GetCustomerDTO(
        List<GetAccountDTO> accounts,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
