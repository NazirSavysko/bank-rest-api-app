package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.EmailVerificationCodes;
import bank.rest.app.bankrestapp.resository.EmailVerificationCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailVerificationCodeRepository codeRepo;

    @InjectMocks
    private EmailServiceImpl emailService;


    @Test
    void verifyCode_Success() {
        // Arrange
        String email = "test@example.com";
        String code = "12345";
        EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setCreatedAt(LocalDateTime.now());

        when(codeRepo.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertDoesNotThrow(() -> emailService.verifyCode(email, code));
    }

    @Test
    void verifyCode_InvalidCode() {
        // Arrange
        String email = "test@example.com";
        EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setEmail(email);
        entity.setCode("54321");
        entity.setCreatedAt(LocalDateTime.now());

        when(codeRepo.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> emailService.verifyCode(email, "12345"));
    }

    @Test
    void verifyCode_Expired() {
         // Arrange
        String email = "test@example.com";
        EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setEmail(email);
        entity.setCode("12345");
        entity.setCreatedAt(LocalDateTime.now().minusMinutes(31)); // Assuming 30 min validity

        when(codeRepo.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> emailService.verifyCode(email, "12345"));
    }

    @Test
    void checkIfCodeIsVerified_Success() {
        // Arrange
        String email = "test@example.com";
        EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setVerified(false);
        entity.setCreatedAt(LocalDateTime.now());

        when(codeRepo.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act
        emailService.checkIfCodeIsVerified(email);

        // Assert
        verify(codeRepo).delete(entity);
    }

    @Test
    void checkIfCodeIsVerified_AlreadyVerified() {
         // Arrange
        String email = "test@example.com";
        EmailVerificationCodes entity = new EmailVerificationCodes();
        entity.setVerified(true); // Already verified
        entity.setCreatedAt(LocalDateTime.now());

        when(codeRepo.findByEmail(email)).thenReturn(Optional.of(entity));

        // Act & Assert
         assertThrows(IllegalArgumentException.class, () -> emailService.checkIfCodeIsVerified(email));
    }

}