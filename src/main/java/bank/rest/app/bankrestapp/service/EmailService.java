package bank.rest.app.bankrestapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.NESTED;

public interface EmailService {
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(fixedRate = 3600000)
    void deleteExpiredCodes();

    void verifyCode(String email, String inputCode);

    @Transactional(rollbackFor = Exception.class)
    void sendVerificationCode(String email);

    void checkIfCodeIsVerified(final String email);
}
