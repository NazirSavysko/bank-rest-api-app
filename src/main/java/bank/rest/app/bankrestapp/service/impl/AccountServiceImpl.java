package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static bank.rest.app.bankrestapp.entity.enums.AccountStatus.ACTIVE;
import static bank.rest.app.bankrestapp.entity.enums.Currency.valueOf;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

@Service
public final class AccountServiceImpl implements AccountService {


    private static final String ACCOUNT_NUMBER_PATTERN = "%034d";

    private static final int ACCOUNT_BALANCE_INITIAL = 100_000_000;

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account generateAccountByCurrencyCode(final @NotNull String currency) {
        final String accountNumber = this.generateAccountNumber();
        final BigDecimal balance = new BigDecimal(ACCOUNT_BALANCE_INITIAL);
        final Currency currencyEnum = valueOf(currency.toUpperCase());

        return Account.builder()
                .accountNumber(accountNumber)
                .balance(balance)
                .currencyCode(currencyEnum)
                .status(ACTIVE)
                .createdAt(now())
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
