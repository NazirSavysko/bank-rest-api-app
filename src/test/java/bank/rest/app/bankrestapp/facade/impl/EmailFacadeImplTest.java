package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;
import bank.rest.app.bankrestapp.service.EmailService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EmailFacadeImpl}.
 * These tests focus on orchestration: ensuring the facade validates DTOs and delegates to the EmailService
 * and that the order of calls is correct. They also ensure that when validation fails the service is not invoked.
 */
@ExtendWith(MockitoExtension.class)
class EmailFacadeImplTest {

    @Mock
    private EmailService emailService;

    @Mock
    private DtoValidator dtoValidator;

    @Mock
    private BindingResult bindingResult;

    private EmailFacadeImpl sut;

    @BeforeEach
    void setUp() {
        this.sut = new EmailFacadeImpl(emailService, dtoValidator);
    }

    @Test
    void sendVerificationCode_shouldValidate_thenCallService_withExactEmail() {
        // given
        EmailDTO input = new EmailDTO("john.doe@example.com");

        // when
        sut.sendVerificationCode(input, bindingResult);

        // then
        InOrder inOrder = inOrder(dtoValidator, emailService);
        inOrder.verify(dtoValidator).validate(input, bindingResult);
        inOrder.verify(emailService).sendVerificationCode("john.doe@example.com");

        verifyNoMoreInteractions(emailService);
        // dtoValidator might have other internals, but facade should call it once with these args
        verify(dtoValidator, times(1)).validate(input, bindingResult);
    }

    @Test
    void sendVerificationCode_whenValidatorThrows_shouldNotCallService_andPropagate() {
        // given
        EmailDTO input = new EmailDTO("bad@example.com");
        RuntimeException validationException = new RuntimeException("validation failed");
        doThrow(validationException).when(dtoValidator).validate(input, bindingResult);

        // when / then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> sut.sendVerificationCode(input, bindingResult));
        assertEquals(validationException, thrown);

        verify(dtoValidator).validate(input, bindingResult);
        verifyNoInteractions(emailService);
    }

    @Test
    void verifyCode_shouldValidate_thenCallService_withExactArgs() {
        // given
        VerifyCodeDTO input = new VerifyCodeDTO("alice@example.com", "ABCDE");

        // when
        sut.verifyCode(input, bindingResult);

        // then
        InOrder inOrder = inOrder(dtoValidator, emailService);
        inOrder.verify(dtoValidator).validate(input, bindingResult);
        inOrder.verify(emailService).verifyCode("alice@example.com", "ABCDE");

        verifyNoMoreInteractions(emailService);
        verify(dtoValidator, times(1)).validate(input, bindingResult);
    }

    @Test
    void verifyCode_whenValidatorThrows_shouldNotCallService_andPropagate() {
        // given
        VerifyCodeDTO input = new VerifyCodeDTO("x@example.com", "12345");
        RuntimeException validationException = new RuntimeException("validation failed");
        doThrow(validationException).when(dtoValidator).validate(input, bindingResult);

        // when / then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> sut.verifyCode(input, bindingResult));
        assertEquals(validationException, thrown);

        verify(dtoValidator).validate(input, bindingResult);
        verifyNoInteractions(emailService);
    }
}
