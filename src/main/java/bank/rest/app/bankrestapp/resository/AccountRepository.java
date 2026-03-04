package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByAccountNumber(String accountNumber);

    @Lock(PESSIMISTIC_WRITE)
    Optional<Account> findByCard_CardNumber(String cardCardNumber);

    Optional<Account> findByAccountNumber(String accountNumber);
}
