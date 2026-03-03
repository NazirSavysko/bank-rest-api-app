package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
