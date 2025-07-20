package bank.rest.app.bankrestapp.facade.impl;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;
import bank.rest.app.bankrestapp.facade.EmailFacade;
import bank.rest.app.bankrestapp.service.EmailService;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class EmailFacadeImpl implements EmailFacade {

    private final EmailService emailService;
    private final DtoValidator dtoValidator;

    @Autowired
    public EmailFacadeImpl(final EmailService emailService, final DtoValidator dtoValidator) {
        this.emailService = emailService;
        this.dtoValidator = dtoValidator;
    }

    @Override
    public void sendVerificationCode(final @NotNull EmailDTO email,
                                     final BindingResult bindingResult) {
        this.dtoValidator.validate(email, bindingResult);

        emailService.sendVerificationCode(email.email());
    }

    @Override
    public void verifyCode(final @NotNull VerifyCodeDTO verifyCodeDTO,
                           final BindingResult bindingResult) {
        this.dtoValidator.validate(verifyCodeDTO, bindingResult);

        this.emailService.verifyCode(verifyCodeDTO.email(), verifyCodeDTO.code());
    }
}
