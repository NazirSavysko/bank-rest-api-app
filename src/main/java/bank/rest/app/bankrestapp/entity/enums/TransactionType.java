package bank.rest.app.bankrestapp.entity.enums;

/**
 * Enumeration representing the different types of financial transactions.
 *
 * <p>This enum categorizes transactions based on their purpose and operation type.
 * Each transaction type has specific business rules and processing requirements.</p>
 *
 * <p>Transaction types and their descriptions:</p>
 * <ul>
 *   <li>TRANSFER - Moving funds between accounts (internal or external transfers)</li>
 *   <li>PAYMENT - Outgoing payments for services, bills, or purchases</li>
 *   <li>IBAN_PAYMENT - Outgoing payment by IBAN реквізитами</li>
 *   <li>INTERNET_PAYMENT - Outgoing internet provider payment</li>
 *   <li>UTILITY_PAYMENT - Outgoing utility bill payment</li>
 * </ul>
 *
 * <p>Each transaction type may have different validation rules, fees, and
 * processing times depending on the business requirements.</p>
 *
 * @see bank.rest.app.bankrestapp.entity.Transaction
 *
 * @author Nazira Savisska
 * @since 1.0
 */
public enum TransactionType {

    /** Transfer transaction - moving funds between accounts */
    TRANSFER,

    /** Payment transaction - outgoing payment for services or purchases */
    PAYMENT,

    /** Payment transaction created from IBAN payment flow */
    IBAN_PAYMENT,

    /** Payment transaction created from internet payment flow */
    INTERNET_PAYMENT,

    /** Payment transaction created from utility payment flow */
    UTILITY_PAYMENT
}
