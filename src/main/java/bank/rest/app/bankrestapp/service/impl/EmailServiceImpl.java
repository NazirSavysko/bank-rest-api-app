package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.EmailVerificationCodes;
import bank.rest.app.bankrestapp.resository.EmailVerificationCodeRepository;
import bank.rest.app.bankrestapp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.constants.EmailDefaults.EMAIL_CODE_EXPIRATION_MINUTES;
import static bank.rest.app.bankrestapp.constants.EmailDefaults.EMAIL_CODE_VALIDITY_WINDOW_MINUTES;
import static bank.rest.app.bankrestapp.constants.MessageError.*;
import static java.lang.String.valueOf;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationCodeRepository codeRepo;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
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
    public void verifyCode(final String email, final String inputCode) {
        this.codeRepo.findByEmail(email)
                .filter(code -> code.getCode().equals(inputCode))
                .filter(code -> code.getCreatedAt().isAfter(now().minusMinutes(EMAIL_CODE_VALIDITY_WINDOW_MINUTES)))
                .orElseThrow(
                        () -> new NoSuchElementException(ERRORS_EMAIL_CODE_IS_INVALID)
                );
    }

    @Override
    public void sendVerificationCode(final String email) {
        final int generatedCode = (int) (Math.random() * 90000 + 10000);
        final String code = valueOf((generatedCode));

        this.codeRepo.deleteByEmail(email);

        final EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setCreatedAt(now());
        entity.setVerified(false);

        this.codeRepo.save(entity);

        MimeMessage message = this.mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Код підтвердження електронної пошти");
            helper.setText(this.buildEmailContent(code), true);

        } catch (IOException e) {
            throw new UncheckedIOException("Не вдалося зчитати шаблон листа", e);

        } catch (MessagingException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Помилка надсилання листа", e);
        }

        mailSender.send(message);
    }

    @Override
    public void checkIfCodeIsVerified(final String email) {
        final EmailVerificationCodes emailCode = this.codeRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_EMAIL_CODE_IS_INVALID));

        if (emailCode.isVerified()) {
            throw new IllegalArgumentException(ERRORS_EMAIL_NOT_VERIFIED);
        }
        if (emailCode.getCreatedAt().isBefore(now().minusMinutes(EMAIL_CODE_EXPIRATION_MINUTES))) {
            throw new IllegalArgumentException(ERRORS_EMAIL_CODE_IS_EXPIRED);
        }

        this.codeRepo.delete(emailCode);
    }


    private @NotNull String buildEmailContent(String code) throws IOException {
        final ClassPathResource resource = new ClassPathResource("templates/verification-code-template.html");
        final String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        return html.replace("{{CODE}}", code);
    }
}
