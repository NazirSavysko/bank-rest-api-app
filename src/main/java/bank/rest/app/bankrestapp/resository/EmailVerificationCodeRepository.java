package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.EmailVerificationCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Locale;
import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCodes, Integer> {
    Optional<EmailVerificationCodes> findByEmail(String email);

    void deleteByEmail(String email);
}
