package bank.rest.app.bankrestapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.NESTED;

public interface EmailService {
    /**
     * Removes expired email verification codes.
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(fixedRate = 3600000)
    void deleteExpiredCodes();

    /**
     * Verifies the confirmation code submitted for an email address.
     *
     * @param email email address to verify
     * @param inputCode code provided by the user
     * @throws IllegalArgumentException if the code is expired or the email is not verified
     * @throws java.util.NoSuchElementException if the verification code cannot be found
     */
    void verifyCode(String email, String inputCode);

    /**
     * Generates and sends a new verification code to the specified email address.
     *
     * @param email recipient email address
     * @throws RuntimeException if the message cannot be sent
     */
    @Transactional(rollbackFor = Exception.class)
    void sendVerificationCode(String email);

    /**
     * Generates and sends a verification code to the specified email with custom message text.
     *
     * @param email recipient email address
     * @param messageTemplate message template with "{code}" placeholder for generated code
     * @throws RuntimeException if the message cannot be sent
     */
    @Transactional(rollbackFor = Exception.class)
    void sendVerificationCodeWithMessage(String email, String messageTemplate);

    /**
     * Ensures that the email address has already passed verification.
     *
     * @param email email address to check
     * @throws IllegalArgumentException if the email has not been verified or the code expired
     * @throws java.util.NoSuchElementException if no verification record exists
     */
    void checkIfCodeIsVerified(final String email);
}
