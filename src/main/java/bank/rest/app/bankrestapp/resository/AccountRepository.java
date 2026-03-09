package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByEdrpou(String edrpou);

    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    Optional<Account> findByCard_CardNumber(String cardCardNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    @Query("select a from Account a where a.card.cardNumber = :cardNumber")
    Optional<Account> findByCard_CardNumberForUpdate(@Param("cardNumber") String cardNumber);

    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    Optional<Account> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    @Query("select a from Account a where a.accountNumber = :accountNumber")
    Optional<Account> findWithLockByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    @Query("select a from Account a where a.accountId = :accountId")
    Optional<Account> findByIdForUpdate(@Param("accountId") Integer accountId);

    @Override
    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    Optional<Account> findById(Integer integer);
}
