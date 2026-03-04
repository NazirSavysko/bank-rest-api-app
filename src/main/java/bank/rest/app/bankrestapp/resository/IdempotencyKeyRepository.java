package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
}
