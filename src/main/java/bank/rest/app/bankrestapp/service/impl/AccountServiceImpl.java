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

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

import static bank.rest.app.bankrestapp.constants.AccountDefaults.ACCOUNT_BALANCE_INITIAL;
import static bank.rest.app.bankrestapp.constants.AccountDefaults.ACCOUNT_NUMBER_PATTERN;
import static bank.rest.app.bankrestapp.constants.AccountDefaults.DEFAULT_ACCOUNT_STATUS;
import static bank.rest.app.bankrestapp.constants.AccountDefaults.DEFAULT_CREATED_AT;
import static bank.rest.app.bankrestapp.constants.AccountDefaults.FOP_ACCOUNT_BALANCE_INITIAL;
import static bank.rest.app.bankrestapp.constants.AccountDefaults.MAXIMUM_NUMBER_OF_ACCOUNTS;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_NOT_FOUND_BY_NUMBER;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_WITH_CURRENCY_ALREADY_EXISTS;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_CUSTOMER_NOT_FOUND_BY_EMAIL;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_MAXIMUM_NUMBER_OF_ACCOUNTS_REACHED;
import static java.lang.String.format;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardService cardService;

    @Override
    public Account generateAccountByCurrencyCode(final @NotNull Currency currency) {
        return Account.builder()
                .accountNumber(this.generateAccountNumber(this.getBeginningOfWordByCurrency(currency)))
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
        final boolean isFopAccount = this.isFopAccount(accountType);
        final Currency currency = this.resolveCurrency(accountType, currencyCode);
        final Customer customer = this.getCustomerByEmail(customerEmail);

        this.validateAccountCapacity(customer);
        this.validateRequestedAccountAbsence(customer, currency, isFopAccount);

        final Account account = this.generateAccountByCurrencyCode(currency);
        final Card card = this.cardService.generateCard();

        this.assignAccountDetails(account, card, customer, isFopAccount);

        return this.accountRepository.save(account);
    }

    @Override
    public Account getAccountByNumber(final String accountNumber) {
        return this.accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_ACCOUNT_NOT_FOUND_BY_NUMBER));
    }

    private Currency resolveCurrency(final String accountType, final String currencyCode) {
        return this.isFopAccount(accountType)
                ? Currency.UAH
                : Currency.valueOf(currencyCode.toUpperCase());
    }

    private boolean isFopAccount(final String accountType) {
        return AccountType.FOP.name().equalsIgnoreCase(accountType);
    }

    private Customer getCustomerByEmail(final String customerEmail) {
        return this.customerRepository.findByAuthUserEmail(customerEmail)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_CUSTOMER_NOT_FOUND_BY_EMAIL));
    }

    private void validateAccountCapacity(final Customer customer) {
        if (customer.getAccounts().size() >= MAXIMUM_NUMBER_OF_ACCOUNTS) {
            throw new IllegalArgumentException(ERRORS_MAXIMUM_NUMBER_OF_ACCOUNTS_REACHED);
        }
    }

    private void validateRequestedAccountAbsence(final Customer customer,
                                                 final Currency requestedCurrency,
                                                 final boolean requestedFopAccount) {
        final boolean existingAccountPresent = customer.getAccounts().stream()
                .anyMatch(account -> this.hasExistingRequestedAccount(account, requestedCurrency, requestedFopAccount));

        if (existingAccountPresent) {
            throw new IllegalArgumentException(ERRORS_ACCOUNT_WITH_CURRENCY_ALREADY_EXISTS);
        }
    }

    private void assignAccountDetails(final Account account,
                                      final Card card,
                                      final Customer customer,
                                      final boolean isFopAccount) {
        account.setAccountType(isFopAccount ? AccountType.FOP : AccountType.CURRENT);
        account.setBalance(isFopAccount ? FOP_ACCOUNT_BALANCE_INITIAL : ACCOUNT_BALANCE_INITIAL);
        account.setCustomer(customer);
        account.setCard(card);
        account.setSentTransactions(new ArrayList<>());
        account.setReceivedTransactions(new ArrayList<>());
        card.setAccount(account);
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

    private String generateAccountNumber(final String beginningOfWord) {
        String accountNumber;

        do {
            final long randomNumber = (long) (Math.random() * 1_0000_0000_0000_0000L);
            accountNumber = format(ACCOUNT_NUMBER_PATTERN, beginningOfWord, randomNumber);
        } while (this.accountRepository.existsByAccountNumber(accountNumber));

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
    private @NotNull String getBeginningOfWordByCurrency(final @NotNull Currency currency) {
        return "UA";
    }
}
