package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import org.springframework.transaction.annotation.Transactional;

public interface AccountService {

    /**
     * Creates a new account entity prefilled with generated identifiers and default values
     * for the requested currency.
     *
     * @param currency currency of the account to generate
     * @return generated account entity ready for additional association setup
     */
    Account generateAccountByCurrencyCode(Currency currency);

    /**
     * Creates and persists a new customer account of the requested type.
     *
     * @param accountType type of account to create
     * @param currencyCode requested currency code for non-FOP accounts
     * @param customerEmail email of the customer who will own the account
     * @return persisted account entity
     * @throws IllegalArgumentException if the customer reached the account limit or already has a matching account
     * @throws java.util.NoSuchElementException if the customer cannot be found by email
     */
    @Transactional(rollbackFor = Exception.class)
    Account createAccount(String accountType, String currencyCode, String customerEmail);

    /**
     * Finds an account by its account number.
     *
     * @param accountNumber account number to search by
     * @return found account entity
     * @throws java.util.NoSuchElementException if no account exists for the provided number
     */
    Account getAccountByNumber(String accountNumber);
}
