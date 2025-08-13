package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.CardService;
import lombok.AllArgsConstructor;
import org.hibernate.mapping.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.NoSuchElementException;
import java.util.Optional;

import static bank.rest.app.bankrestapp.constants.AccountDefaults.*;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_INVALID_EMAIL;
import static java.lang.String.format;
import static java.util.List.of;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardService cardService;


    @Override
    public Account generateAccountByCurrencyCode(final @NotNull Currency currency) {
        final String beginningOfWord = this.getBeginningOfWordByCurrency(currency);
        final String accountNumber = this.generateAccountNumber(beginningOfWord);

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

        if(customer.getAccounts().size() >= MAXIMUM_NUMBER_OF_ACCOUNTS) {
            throw new IllegalArgumentException("Customer has reached the maximum number of accounts.");
        }
        Optional<Account> existingAccount = customer.getAccounts().stream().filter(account1 ->
                account1.getCurrencyCode() == currency).findFirst();
        if (existingAccount.isPresent()) {
            throw new IllegalArgumentException("Customer already has an account with this currency.");
        }

        account.setCustomer(customer);
        account.setCard(card);
        card.setAccount(account);
        account.setSentTransactions(of());
        account.setReceivedTransactions(of());

        return this.accountRepository.save(account);
    }

    private String generateAccountNumber(String beginningOfWord) {
        String accountNumber;

        do {
            final long randomNumber = (long) (Math.random() * 1_0000_0000_0000_0000L);
            accountNumber = format(ACCOUNT_NUMBER_PATTERN,beginningOfWord, randomNumber);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    @Contract(pure = true)
    private @NotNull String getBeginningOfWordByCurrency(@NotNull Currency currency) {
        return switch (currency) {
            case UAH -> "UA";
            case USD -> "US";
            case EUR -> "EU";
        };
    }
}
