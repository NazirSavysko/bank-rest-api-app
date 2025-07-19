package bank.rest.app.bankrestapp.dto;

import java.util.List;

public record CustomerDTO(
        List<AccountDTO> accounts,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
