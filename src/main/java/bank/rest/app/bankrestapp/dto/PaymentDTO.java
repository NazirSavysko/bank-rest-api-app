package bank.rest.app.bankrestapp.dto;

public record PaymentDTO(
        String concurrency,
        String amount,
        String beneficiaryName,
        String purpose
) {
}
