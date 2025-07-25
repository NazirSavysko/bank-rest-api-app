package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.CardService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.constants.AccountDefaults.*;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_INVALID_EMAIL;
import static java.lang.String.format;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardService cardService;

    @Autowired
    public AccountServiceImpl(final AccountRepository accountRepository,
                              final CardService cardService,
                              final CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.cardService = cardService;
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

    @Override
    public Account createAccount(final @NotNull String accountType, final String customerEmail) {
        final Currency currency = Currency.valueOf(accountType.toUpperCase());
        final Account account = this.generateAccountByCurrencyCode(currency);
        final Card card = this.cardService.generateCard();

        final Customer customer = this.customerRepository.findByAuthUserEmail(customerEmail)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_INVALID_EMAIL));

        account.setCustomer(customer);
        account.setCard(card);
        card.setAccount(account);

        return this.accountRepository.save(account);
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
