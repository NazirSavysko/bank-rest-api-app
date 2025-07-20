package bank.rest.app.bankrestapp.constants;

/**
 * Utility class containing default values and constants for bank card creation and management.
 * This class provides standardized default values used throughout the banking application
 * for card initialization, formatting, and configuration.
 *
 * <p>The class contains constants for:</p>
 * <ul>
 *   <li>Card number formatting patterns</li>
 *   <li>CVV code formatting patterns</li>
 *   <li>Card expiration period settings</li>
 * </ul>
 *
 * <p>All constants are public, static, and final to ensure they cannot be modified
 * and can be accessed without instantiating the class. These constants ensure
 * consistent card number generation and validation across the banking system.</p>
 *
 * <h3>Usage in CardServiceImpl:</h3>
 * <p>This class is primarily used in {@link bank.rest.app.bankrestapp.service.impl.CardServiceImpl}
 * for card generation operations:</p>
 * <pre>{@code
 * // Generating card number with 16-digit format
 * cardNumber = format(CARD_NUMBER_PATTERN, randomNumber);
 *
 * // Generating CVV with 3-digit format
 * return format(CVV_NUMBER_PATTERN, (int) (random() * 1000));
 *
 * // Setting card expiry date
 * final LocalDateTime expiryDate = now().plusYears(CARD_EXPIRY_YEARS);
 * }</pre>
 *
 * @author Savysko Nazir
 * @version 1.0
 * @since 1.0
 * @see bank.rest.app.bankrestapp.entity.Card
 * @see bank.rest.app.bankrestapp.service.impl.CardServiceImpl
 */
public final class CardDefaults {

    /**
     * Pattern for formatting card numbers with leading zeros.
     * Creates a 16-digit string with zero-padding for consistent card number format.
     *
     * <p>Example: card ID 12345 becomes "0000000000012345"</p>
     * <p>This follows standard payment card numbering formats used by major card networks.</p>
     */
    public static final String CARD_NUMBER_PATTERN = "%016d";

    /**
     * Pattern for formatting CVV (Card Verification Value) codes with leading zeros.
     * Creates a 3-digit string with zero-padding for consistent CVV format.
     *
     * <p>Example: CVV 45 becomes "045"</p>
     * <p>This ensures all CVV codes are exactly 3 digits as required by payment standards.</p>
     */
    public static final String CVV_NUMBER_PATTERN = "%03d";

    /**
     * Default expiration period for newly issued cards in years.
     * Cards are valid for 5 years from the date of issuance.
     *
     * <p>This is a standard validity period used by most banking institutions
     * for debit and credit cards.</p>
     */
    public static final int CARD_EXPIRY_YEARS = 5;

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class is designed to be used only for its static constants.
     */
    private CardDefaults() {
    }
}
