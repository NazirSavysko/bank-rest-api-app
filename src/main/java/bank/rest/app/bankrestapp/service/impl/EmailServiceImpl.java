package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.EmailVerificationCodes;
import bank.rest.app.bankrestapp.resository.EmailVerificationCodeRepository;
import bank.rest.app.bankrestapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationCodeRepository codeRepo;

    @Autowired
    public EmailServiceImpl(final JavaMailSender mailSender, final EmailVerificationCodeRepository codeRepo) {
        this.mailSender = mailSender;
        this.codeRepo = codeRepo;
    }

    @Override
    public void deleteExpiredCodes() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
        List<EmailVerificationCodes> expired = codeRepo.findAll().stream()
                .filter(code -> code.getCreatedAt().isBefore(cutoff))
                .toList();

        codeRepo.deleteAll(expired);
    }

    @Override
    public boolean verifyCode(String email, String inputCode) {
        return codeRepo.findByEmail(email)
                .filter(code -> code.getVerificationCode().equals(inputCode))
                .filter(code -> code.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5)))
                .isPresent();
    }

    @Override
    public void sendVerificationCode(final String email) {
        String code = String.valueOf((int)(Math.random() * 90000 + 10000));

        codeRepo.deleteByEmail(email);

        EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setEmail(email);
        entity.setVerificationCode(code);
        codeRepo.save(entity);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Код підтвердження реєстрації");
        message.setText("Ваш код підтвердження: " + code);
        mailSender.send(message);
    }
}
