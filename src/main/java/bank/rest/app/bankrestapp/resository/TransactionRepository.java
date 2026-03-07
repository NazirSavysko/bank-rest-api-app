package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.resository.projection.HistoryItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber OR (t.toAccount.accountNumber = :accountNumber AND t.status NOT IN :statuses)")
    Page<Transaction> findAllTransactions(@Param("accountNumber") String accountNumber, @Param("statuses") Collection<TransactionStatus> statuses, Pageable pageable);

    @Query(
            value = """
                    SELECT * FROM (
                        SELECT t.transaction_id AS id,
                               t.amount AS amount,
                               t.currency_code AS currency,
                               t.transaction_date AS created_at,
                               'TRANSFER' AS type,
                               t.from_account_id AS sender_account_id,
                               t.to_account_id AS receiver_account_id,
                               t.description AS details
                        FROM transaction t
                        WHERE (:filter = 'ALL' OR :filter = 'TRANSFERS')
                          AND (t.from_account_id = :accountId OR t.to_account_id = :accountId)
                        UNION ALL
                        SELECT p.payment_id AS id,
                               p.amount AS amount,
                               p.currency_code AS currency,
                               p.payment_date AS created_at,
                               CASE
                                   WHEN p.beneficiary_acc IS NOT NULL AND LENGTH(TRIM(p.beneficiary_acc)) > 0 THEN 'IBAN_PAYMENT'
                                   ELSE 'INTERNET_PAYMENT'
                               END AS type,
                               p.account_id AS sender_account_id,
                               NULL AS receiver_account_id,
                               COALESCE(NULLIF(p.beneficiary_acc, ''), p.beneficiary_name, p.purpose) AS details
                        FROM payment p
                        WHERE (:filter = 'ALL' OR :filter = 'PAYMENTS')
                          AND p.account_id = :accountId
                    ) AS history_items
                    """,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT t.transaction_id
                        FROM transaction t
                        WHERE (:filter = 'ALL' OR :filter = 'TRANSFERS')
                          AND (t.from_account_id = :accountId OR t.to_account_id = :accountId)
                        UNION ALL
                        SELECT p.payment_id
                        FROM payment p
                        WHERE (:filter = 'ALL' OR :filter = 'PAYMENTS')
                          AND p.account_id = :accountId
                    ) AS history_count
                    """,
            nativeQuery = true
    )
    Page<HistoryItemProjection> findAccountHistory(@Param("accountId") Integer accountId, @Param("filter") String filter, Pageable pageable);
}
