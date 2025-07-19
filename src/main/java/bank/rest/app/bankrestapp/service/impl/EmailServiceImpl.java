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
import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.validation.MessageError.ERRORS_EMAIL_CODE_IS_INVALID;
import static java.lang.String.valueOf;
import static java.time.LocalDateTime.now;

@Service
public class EmailServiceImpl implements EmailService {

    private static final int EMAIL_CODE_EXPIRATION_MINUTES = 10;
    private static final int EMAIL_CODE_VALIDITY_WINDOW_MINUTES = 5;

    private final JavaMailSender mailSender;
    private final EmailVerificationCodeRepository codeRepo;

    @Autowired
    @SuppressWarnings( "SpringJavaInjectionPointsAutowiringInspection")
    public EmailServiceImpl(final JavaMailSender mailSender, final EmailVerificationCodeRepository codeRepo) {
        this.mailSender = mailSender;
        this.codeRepo = codeRepo;
    }

    @Override
    public void deleteExpiredCodes() {
        final LocalDateTime cutoff = now().minusMinutes(EMAIL_CODE_EXPIRATION_MINUTES);
        final List<EmailVerificationCodes> expired = codeRepo.findAll().stream()
                .filter(code -> code.getCreatedAt().isBefore(cutoff))
                .toList();

        this.codeRepo.deleteAll(expired);
    }

    @Override
    public void verifyCode(final String email,final String inputCode) {
         this.codeRepo.findByEmail(email)
                .filter(code -> code.getCode().equals(inputCode))
                .filter(code -> code.getCreatedAt().isAfter(now().minusMinutes(EMAIL_CODE_VALIDITY_WINDOW_MINUTES)))
                .orElseThrow(
                        () -> new NoSuchElementException(ERRORS_EMAIL_CODE_IS_INVALID)
                );
    }

    @Override
    public void sendVerificationCode(final String email) {
        final int generatedCode = (int)(Math.random() * 90000 + 10000);
        final String code = valueOf((generatedCode));

        this.codeRepo.deleteByEmail(email);

        final EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setCreatedAt(now());
        entity.setVerified(false);
        this.codeRepo.save(entity);

        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Код підтвердження реєстрації");
        message.setText("Ваш код підтвердження: " + code);
        this.mailSender.send(message);
    }
}
