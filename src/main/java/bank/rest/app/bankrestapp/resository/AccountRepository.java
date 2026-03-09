package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByEdrpou(String edrpou);

    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    Optional<Account> findByCard_CardNumber(String cardCardNumber);

    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    Optional<Account> findByAccountNumber(String accountNumber);

    @Override
    @EntityGraph(attributePaths = {"customer", "customer.authUser", "card"})
    Optional<Account> findById(Integer integer);
}
