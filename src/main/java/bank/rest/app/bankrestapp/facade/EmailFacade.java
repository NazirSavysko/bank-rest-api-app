package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;
import org.springframework.validation.BindingResult;

public interface EmailFacade {

    void sendVerificationCode(EmailDTO email, final BindingResult bindingResult);

    void verifyCode(VerifyCodeDTO verifyCodeDTO, final BindingResult bindingResult);

}
