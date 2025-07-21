package bank.rest.app.bankrestapp.entity.enums;

/**
 * Enumeration representing supported currency types in the banking system.
 *
 * <p>This enum defines all currencies that can be used for bank accounts,
 * transactions, and financial operations within the system. Each currency
 * represents a specific monetary unit with its ISO 4217 currency code.</p>
 *
 * <p>Currently supported currencies:</p>
 * <ul>
 *   <li>UAH - Ukrainian Hryvnia (₴)</li>
 *   <li>USD - United States Dollar ($)</li>
 *   <li>EUR - Euro (€)</li>
 * </ul>
 *
 * <p>This enum is used in conjunction with the {@code @Currency} annotation
 * for validation purposes in DTOs.</p>
 *
 * @see bank.rest.app.bankrestapp.entity.annotation.Currency
 * @see bank.rest.app.bankrestapp.entity.Account
 *
 * @author Nazira Savisska
 * @since 1.0
 */
public enum Currency {

    /** Ukrainian Hryvnia - official currency of Ukraine */
    UAH,

    /** United States Dollar - official currency of the United States */
    USD,

    /** Euro - official currency of the European Union */
    EUR
}
