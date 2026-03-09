package bank.rest.app.bankrestapp.controller.payload;

import bank.rest.app.bankrestapp.entity.enums.AccountType;

public record CreateAccountPayload(
        AccountType accountType,
        String currency
) {
}
