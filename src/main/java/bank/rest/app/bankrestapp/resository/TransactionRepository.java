package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
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
