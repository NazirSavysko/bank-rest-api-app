package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByAccountNumber(String accountNumber);
}
