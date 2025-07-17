package bank.rest.app.bankrestapp.dto;

public record CreateCustomerDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber
){
}
