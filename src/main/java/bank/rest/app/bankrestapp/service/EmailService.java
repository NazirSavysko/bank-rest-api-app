package bank.rest.app.bankrestapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public interface EmailService {
    @Scheduled(fixedRate = 3600000) // раз в час
    @Transactional
    void deleteExpiredCodes();

    boolean verifyCode(String email, String inputCode);

    @Transactional
    void sendVerificationCode(String email);
}
