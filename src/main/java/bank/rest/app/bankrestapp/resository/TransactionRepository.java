package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.resository.projection.TransactionHistoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber OR (t.toAccount.accountNumber = :accountNumber AND t.status NOT IN :statuses)")
    Page<Transaction> findAllTransactions(@Param("accountNumber") String accountNumber, @Param("statuses") Collection<TransactionStatus> statuses, Pageable pageable);

    @Query(
            value = """
                    SELECT history.item_type AS itemType,
                           history.operation_id AS operationId,
                           history.amount AS amount,
                           history.currency_code AS currencyCode,
                           history.status AS status,
                           history.description AS description,
                           history.created_at AS createdAt,
                           history.sender_account_id AS senderAccountId,
                           history.receiver_account_id AS receiverAccountId,
                           history.recipient_iban AS recipientIban,
                           history.recipient_name AS recipientName,
                           history.provider_name AS providerName,
                           history.contract_number AS contractNumber
                    FROM (
                        SELECT 'TRANSFER' AS item_type,
                               CAST(t.transaction_id AS BIGINT) AS operation_id,
                               t.amount AS amount,
                               t.currency_code AS currency_code,
                               t.status AS status,
                               t.description AS description,
                               t.transaction_date AS created_at,
                               t.from_account_id AS sender_account_id,
                               t.to_account_id AS receiver_account_id,
                               NULL AS recipient_iban,
                               NULL AS recipient_name,
                               NULL AS provider_name,
                               NULL AS contract_number
                        FROM transaction t
                        WHERE t.from_account_id = :accountId
                           OR t.to_account_id = :accountId

                        UNION ALL

                        SELECT CASE
                                   WHEN p.payment_type = 'IBAN' THEN 'IBAN_PAYMENT'
                                   WHEN p.payment_type = 'INTERNET' THEN 'INTERNET_PAYMENT'
                               END AS item_type,
                               CAST(p.payment_id AS BIGINT) AS operation_id,
                               p.amount AS amount,
                               p.currency_code AS currency_code,
                               p.status AS status,
                               p.purpose AS description,
                               p.payment_date AS created_at,
                               p.account_id AS sender_account_id,
                               CASE
                                    WHEN p.beneficiary_acc = acc.account_number THEN :accountId
                                    ELSE NULL
                                END AS receiver_account_id,
                               p.recipient_iban AS recipient_iban,
                               p.recipient_name AS recipient_name,
                               p.provider_name AS provider_name,
                               p.contract_number AS contract_number
                        FROM payment p
                        CROSS JOIN account acc
                        WHERE p.payment_type IN ('IBAN', 'INTERNET')
                          AND acc.account_id = :accountId
                          AND (
                                p.account_id = :accountId
                                OR p.beneficiary_acc = acc.account_number
                              )
                    ) history
                    ORDER BY history.created_at DESC, history.operation_id DESC
                    """,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT t.transaction_id
                        FROM transaction t
                        WHERE t.from_account_id = :accountId
                           OR t.to_account_id = :accountId

                        UNION ALL

                        SELECT p.payment_id
                        FROM payment p
                        CROSS JOIN account acc
                        WHERE p.payment_type IN ('IBAN', 'INTERNET')
                          AND acc.account_id = :accountId
                          AND (
                                p.account_id = :accountId
                                OR p.beneficiary_acc = acc.account_number
                              )
                    ) history_count
                    """,
            nativeQuery = true
    )
    Page<TransactionHistoryProjection> findAccountHistory(@Param("accountId") Integer accountId, Pageable pageable);

    @Query("""
        SELECT t FROM Transaction t
        LEFT JOIN t.account acc
        LEFT JOIN t.toAccount toAcc
        WHERE (acc.accountNumber = :accountNumber OR toAcc.accountNumber = :accountNumber)
          AND t.transactionDate >= :startDate
          AND t.transactionDate < :endDate
          AND t.status = :status
        """)
    List<Transaction> findMonthlyTransactions(@Param("accountNumber") String accountNumber,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate,
                                              @Param("status") TransactionStatus status);

    List<Transaction> findByAccount_AccountNumber(String accountNumber);
}
