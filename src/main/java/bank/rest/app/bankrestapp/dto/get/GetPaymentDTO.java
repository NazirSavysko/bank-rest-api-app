package bank.rest.app.bankrestapp.dto.get;

public record GetPaymentDTO(
        String concurrency,
        String amount,
        String beneficiaryName,
        String purpose
) {
}
