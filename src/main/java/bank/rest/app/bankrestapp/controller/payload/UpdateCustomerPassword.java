package bank.rest.app.bankrestapp.controller.payload;

public record UpdateCustomerPassword(
        String oldPassword,
        String newPassword
) {
}
