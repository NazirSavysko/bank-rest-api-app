package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByCard_CardNumber(String cardCardNumber);

    Optional<Account> findByAccountNumber(String accountNumber);
}
