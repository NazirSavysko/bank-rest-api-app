package bank.rest.app.bankrestapp.resository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<bank.rest.app.bankrestapp.entity.Card, Integer> {

 boolean existsByCardNumber(String cardNumber);
}
