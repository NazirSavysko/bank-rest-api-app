package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByCard_CardNumber(String cardCardNumber);
}
