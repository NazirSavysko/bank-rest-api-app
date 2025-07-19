package bank.rest.app.bankrestapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public interface EmailService {
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(fixedRate = 3600000)
    void deleteExpiredCodes();

    void verifyCode(String email, String inputCode);

    void sendVerificationCode(String email);

    void checkIfCodeIsVerified(final String email);
}
