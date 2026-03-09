package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Customer;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @NotNull Optional<Customer> findById(@NotNull Integer id);

    @EntityGraph(attributePaths = {"authUser", "accounts"})
    Optional<Customer> findByAuthUserEmail(String email);

    Optional<Customer> findAllByPhone(String phone);

}
