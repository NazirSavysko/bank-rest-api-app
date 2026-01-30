package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import org.springframework.transaction.annotation.Transactional;

public interface AccountService {

    Account generateAccountByCurrencyCode(Currency currency);

    @Transactional(rollbackFor = Exception.class)
    Account createAccount(String accountType,String customerEmail);

    Account getAccountByNumber(String accountNumber);
}
