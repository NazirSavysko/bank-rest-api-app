package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Account;

public interface AccountService {

    Account generateAccountByCurrencyCode(String currency);
}
