package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.enums.AccountType;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.service.AccountService;
import bank.rest.app.bankrestapp.service.CardService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;


import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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
                .accountType(AccountType.CURRENT)
                .currencyCode(currency)
                .edrpou(this.generateUniqueEdrpou())
                .status(DEFAULT_ACCOUNT_STATUS)
                .createdAt(DEFAULT_CREATED_AT)
                .build();
    }

    @Override
    public Account createAccount(final @NotNull String accountType,
                                 final String currencyCode,
                                 final String customerEmail) {
        final boolean isFopAccount = AccountType.FOP.name().equalsIgnoreCase(accountType);
        final Currency currency = isFopAccount ? Currency.UAH : Currency.valueOf(currencyCode.toUpperCase());
        final Account account = this.generateAccountByCurrencyCode(currency);
        final Card card = this.cardService.generateCard();

        final Customer customer = this.customerRepository.findByAuthUserEmail(customerEmail)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_INVALID_EMAIL));

        if(customer.getAccounts().size() >= MAXIMUM_NUMBER_OF_ACCOUNTS) {
            throw new IllegalArgumentException("Customer has reached the maximum number of accounts.");
        }
        Optional<Account> existingAccount = customer.getAccounts().stream().filter(account1 ->
                this.hasExistingRequestedAccount(account1, currency, isFopAccount)).findFirst();
        if (existingAccount.isPresent()) {
            throw new IllegalArgumentException("Customer already has an account with this currency.");
        }

        account.setAccountType(isFopAccount ? AccountType.FOP : AccountType.CURRENT);
        account.setBalance(isFopAccount ? FOP_ACCOUNT_BALANCE_INITIAL : ACCOUNT_BALANCE_INITIAL);
        account.setCustomer(customer);
        account.setCard(card);
        card.setAccount(account);
        account.setSentTransactions(of());
        account.setReceivedTransactions(of());

        return this.accountRepository.save(account);
    }

    @Override
    public Account getAccountByNumber(final String accountNumber) {
        return this.accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException("Account not found for the provided account number"));
    }

    private boolean hasExistingRequestedAccount(final Account existingAccount,
                                                final Currency requestedCurrency,
                                                final boolean requestedFopAccount) {
        if (requestedFopAccount) {
            return AccountType.FOP.equals(existingAccount.getAccountType());
        }

        return existingAccount.getCurrencyCode() == requestedCurrency
                && !AccountType.FOP.equals(existingAccount.getAccountType());
    }

    private String generateAccountNumber(String beginningOfWord) {
        String accountNumber;

        do {
            final long randomNumber = (long) (Math.random() * 1_0000_0000_0000_0000L);
            accountNumber = format(ACCOUNT_NUMBER_PATTERN,beginningOfWord, randomNumber);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private @NotNull String generateUniqueEdrpou() {
        String edrpou;

        do {
            edrpou = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
        } while (this.accountRepository.existsByEdrpou(edrpou));

        return edrpou;
    }

    @Contract(pure = true)
    private @NotNull String getBeginningOfWordByCurrency(@NotNull Currency currency) {
        return "UA";
    }
}
