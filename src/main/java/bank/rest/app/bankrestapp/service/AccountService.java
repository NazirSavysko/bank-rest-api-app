package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.enums.Currency;

public interface AccountService {

    Account generateAccountByCurrencyCode(Currency currency);
}
