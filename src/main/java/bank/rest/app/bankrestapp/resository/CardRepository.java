package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {

 boolean existsByCardNumber(String cardNumber);
}
