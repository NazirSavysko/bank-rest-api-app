package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.EmailVerificationCodes;
import bank.rest.app.bankrestapp.resository.EmailVerificationCodeRepository;
import bank.rest.app.bankrestapp.service.EmailService;
import com.resend.Resend;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.constants.EmailDefaults.EMAIL_CODE_EXPIRATION_MINUTES;
import static bank.rest.app.bankrestapp.constants.EmailDefaults.EMAIL_CODE_VALIDITY_WINDOW_MINUTES;
import static bank.rest.app.bankrestapp.constants.MessageError.*;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailVerificationCodeRepository codeRepo;

    @Value("${resend.api.key}")
    private String apiKey;


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
        final String code = String.valueOf(generatedCode);

        // 2. Оновлюємо базу даних
        this.codeRepo.deleteByEmail(email);

        final EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setCreatedAt(now());
        entity.setVerified(false);

        this.codeRepo.save(entity);

        Resend resend = new Resend(apiKey);

        try {
            org.springframework.core.io.ClassPathResource resource =
                    new org.springframework.core.io.ClassPathResource("templates/verification-code-template.html");

            String htmlContent = new String(resource.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

            htmlContent = htmlContent.replace("{{CODE}}", code);

            com.resend.services.emails.model.CreateEmailOptions sendEmailRequest =
                    com.resend.services.emails.model.CreateEmailOptions.builder()
                            .from("Bank Emulator <no-reply@send.bank-emulator.app>")
                            .to(email)
                            .subject("Код підтвердження електронної пошти")
                            .html(htmlContent)
                            .build();

            resend.emails().send(sendEmailRequest);

        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException("Не вдалося зчитати HTML шаблон", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Помилка відправки листа через Resend API", e);
        }
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
