package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.ResetPasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;
import bank.rest.app.bankrestapp.facade.EmailFacade;
import bank.rest.app.bankrestapp.service.EmailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailFacadeImpl implements EmailFacade {

    private final EmailService emailService;

    @Autowired
    public EmailFacadeImpl(EmailService emailService) {
        this.emailService = emailService;
    }
    @Override
    public void sendVerificationCode(final @NotNull EmailDTO emailDTO) {
        emailService.sendVerificationCode(emailDTO.email());
    }

    @Override
    public boolean verifyCode(final @NotNull VerifyCodeDTO verifyCodeDTO) {
        return emailService.verifyCode(
                verifyCodeDTO.email(),
                verifyCodeDTO.code()
        );
    }
}
