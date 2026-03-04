package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.WithdrawIdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WithdrawIdempotencyRepository extends JpaRepository<WithdrawIdempotencyRecord, Integer> {
    Optional<WithdrawIdempotencyRecord> findByIdempotencyKeyAndEndpoint(String idempotencyKey, String endpoint);
}
