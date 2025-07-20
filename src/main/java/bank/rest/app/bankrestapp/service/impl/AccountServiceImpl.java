package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static bank.rest.app.bankrestapp.constants.AccountDefaults.*;
import static java.lang.String.format;

@Service
public final class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account generateAccountByCurrencyCode(final @NotNull Currency currency) {
        final String accountNumber = this.generateAccountNumber();

        return Account.builder()
                .accountNumber(accountNumber)
                .balance(ACCOUNT_BALANCE_INITIAL)
                .currencyCode(currency)
                .status(DEFAULT_ACCOUNT_STATUS)
                .createdAt(DEFAULT_CREATED_AT)
                .build();
    }

    private String generateAccountNumber() {
        String accountNumber;

        do {
            final long randomNumber = (long) (Math.random() * 1_0000_0000_0000_0000L);
            accountNumber = format(ACCOUNT_NUMBER_PATTERN, randomNumber);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
