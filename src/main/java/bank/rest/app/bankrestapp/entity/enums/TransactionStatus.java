package bank.rest.app.bankrestapp.entity.enums;

/**
 * Enumeration representing the possible statuses of financial transactions.
 *
 * <p>This enum defines the final states that a transaction can reach after
 * processing. It indicates whether a transaction was successfully executed
 * or was terminated before completion.</p>
 *
 * <p>Transaction lifecycle:</p>
 * <ul>
 *   <li>Transaction initiated → Processing → COMPLETED/CANCELLED</li>
 *   <li>COMPLETED - Transaction successfully processed, and funds transferred</li>
 *   <li>CANCELLED - Transaction terminated due to insufficient funds, validation errors, or user cancellation</li>
 * </ul>
 *
 * <p>This status is used for auditing purposes and to provide transaction
 * history to customers and administrators.</p>
 *
 * @see bank.rest.app.bankrestapp.entity.Transaction
 *
 * @author Nazira Savisska
 * @since 1.0
 */
public enum TransactionStatus {

    /** Transaction was successfully processed and completed */
    COMPLETED,

    /** Transaction was canceled or failed during processing */
    CANCELLED
}
