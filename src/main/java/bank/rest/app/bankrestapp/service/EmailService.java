package bank.rest.app.bankrestapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public interface EmailService {
    @Transactional
    @Scheduled(fixedRate = 3600000)
    void deleteExpiredCodes();

    void verifyCode(String email, String inputCode);

    @Transactional
    void sendVerificationCode(String email);
}
