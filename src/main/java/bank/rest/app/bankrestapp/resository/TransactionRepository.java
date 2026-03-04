package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber OR (t.toAccount.accountNumber = :accountNumber AND t.status NOT IN :statuses)")
    Page<Transaction> findAllTransactions(@Param("accountNumber") String accountNumber, @Param("statuses") Collection<TransactionStatus> statuses, Pageable pageable);

    @Query("""
            SELECT
                COALESCE(SUM(CASE WHEN t.toAccount.accountId = :accountId THEN t.amount ELSE 0 END), 0) AS totalIncome,
                COALESCE(SUM(CASE WHEN t.account.accountId = :accountId THEN t.amount ELSE 0 END), 0) AS totalExpense,
                COUNT(t) AS operationsCount
            FROM Transaction t
            WHERE (t.account.accountId = :accountId OR t.toAccount.accountId = :accountId)
              AND t.status NOT IN :statuses
              AND t.transactionDate >= :startDate AND t.transactionDate < :endDate
            """)
    TransactionSummary getMonthlySummary(@Param("accountId") Integer accountId,
                                         @Param("statuses") Collection<TransactionStatus> statuses,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    interface TransactionSummary {
        BigDecimal getTotalIncome();
        BigDecimal getTotalExpense();
        long getOperationsCount();
    }
}
