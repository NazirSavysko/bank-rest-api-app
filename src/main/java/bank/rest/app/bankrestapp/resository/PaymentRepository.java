package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @EntityGraph(attributePaths = {"account"})
    @Query("""
            SELECT p FROM Payment p
            WHERE p.account.accountNumber = :accountNumber
              AND p.paymentDate >= :startDate
              AND p.paymentDate < :endDate
              AND p.status = :status
            """)
    List<Payment> findMonthlyPayments(@Param("accountNumber") String accountNumber,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("status") PaymentStatus status);
}
