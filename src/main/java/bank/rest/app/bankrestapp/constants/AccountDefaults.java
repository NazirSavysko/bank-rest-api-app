package bank.rest.app.bankrestapp.constants;

import bank.rest.app.bankrestapp.entity.enums.AccountStatus;
import bank.rest.app.bankrestapp.entity.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static bank.rest.app.bankrestapp.entity.enums.AccountStatus.ACTIVE;
import static bank.rest.app.bankrestapp.entity.enums.Currency.UAH;
import static java.time.LocalDateTime.now;

/**
 * Utility class containing default values and constants for bank account creation and management.
 * This class provides standardized default values used throughout the banking application
 * for account initialization and configuration.
 *
 * <p>The class contains constants for:</p>
 * <ul>
 *   <li>Account number formatting patterns</li>
 *   <li>Initial account balance amounts</li>
 *   <li>Default account status values</li>
 *   <li>Default currency settings</li>
 *   <li>Account creation timestamps</li>
 * </ul>
 *
 * <p>All constants are public, static, and final to ensure they cannot be modified
 * and can be accessed without instantiating the class.</p>
 *
 * <h3>Usage in AccountServiceImpl:</h3>
 * <p>This class is primarily used in {@link bank.rest.app.bankrestapp.service.impl.AccountServiceImpl}
 * for account generation operations:</p>
 * <pre>{@code
 * // Building new account with default values
 * return Account.builder()
 *     .accountNumber(accountNumber)
 *     .balance(ACCOUNT_BALANCE_INITIAL)
 *     .currencyCode(currency)
 *     .status(DEFAULT_ACCOUNT_STATUS)
 *     .createdAt(DEFAULT_CREATED_AT)
 *     .build();
 *
 * // Generating account number with 34-digit format
 * accountNumber = format(ACCOUNT_NUMBER_PATTERN, randomNumber);
 * }</pre>
 *
 * <h3>Usage in CustomerServiceImpl:</h3>
 * <p>The DEFAULT_CURRENCY constant is also used in {@link bank.rest.app.bankrestapp.service.impl.CustomerServiceImpl}
 * for customer account creation:</p>
 * <pre>{@code
 * // Creating account with default currency during customer registration
 * final Account account = this.accountService.generateAccountByCurrencyCode(DEFAULT_CURRENCY);
 * }</pre>
 *
 * @author Savysko Nazir
 * @version 1.0
 * @since 1.0
 * @see bank.rest.app.bankrestapp.entity.Account
 * @see bank.rest.app.bankrestapp.service.impl.AccountServiceImpl
 * @see bank.rest.app.bankrestapp.service.impl.CustomerServiceImpl
 * @see AccountStatus
 * @see Currency
 */
public final class AccountDefaults {

    /**
     * Pattern for formatting account numbers.
     * The pattern is set to a 34-character string with the first 2 characters
     * being a fixed prefix and the remaining 32 characters being a zero-padded number.
     *
     * <p>Example: "UAC00000000000000000000000000000"</p>
     */
    public static final String ACCOUNT_NUMBER_PATTERN =  "%2s%032d";

    /**
     * Initial balance amount set for new accounts upon creation.
     * Set to 100,000,000 units in the selected currency.
     *
     */
    public static final BigDecimal ACCOUNT_BALANCE_INITIAL = new BigDecimal(100_000_000);

    /**
     * Default status assigned to newly created accounts.
     * Accounts are created in ACTIVE status, allowing immediate transactions.
     */
    public static final AccountStatus DEFAULT_ACCOUNT_STATUS = ACTIVE;

    /**
     * Default timestamp for account creation.
     * Uses the current system time when the class is loaded.
     *
     * <p><strong>Note:</strong> This value is set once when the class is first loaded,
     * not when each account is created. For actual account creation timestamps,
     * consider using {@code LocalDateTime.now()} directly.</p>
     */
    public static final LocalDateTime DEFAULT_CREATED_AT = now();

    /**
     * Default currency for new accounts.
     * Set to Ukrainian Hryvnia (UAH) as the standard currency for the banking system.
     */
    public static final Currency DEFAULT_CURRENCY = UAH;

    public static final int MAXIMUM_NUMBER_OF_ACCOUNTS = 3;

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class is designed to be used only for its static constants.
     */
    private AccountDefaults() {
        // Prevent instantiation
    }
}
