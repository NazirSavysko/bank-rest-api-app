package bank.rest.app.bankrestapp.entity.enums;

/**
 * Enumeration representing the various statuses that a bank account can have.
 *
 * <p>This enum defines all possible states of a bank account throughout its lifecycle,
 * from creation to closure. Each status affects the account's functionality and
 * available operations.</p>
 *
 * <p>Status transitions typically follow this flow:</p>
 * <ul>
 *   <li>PENDING → ACTIVE (after account verification)</li>
 *   <li>ACTIVE → FROZEN/BLOCKED/SUSPENDED (due to security or compliance issues)</li>
 *   <li>Any status → CLOSED (permanent account closure)</li>
 * </ul>
 *
 * @see bank.rest.app.bankrestapp.entity.Account
 *
 * @author Nazira Savisska
 * @since 1.0
 */
public enum AccountStatus {

    /** Account is active and fully functional for all operations */
    ACTIVE,

    /** Account is blocked due to security concerns or policy violations */
    BLOCKED
}
