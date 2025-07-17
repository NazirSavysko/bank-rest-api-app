package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.ResetPasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;

public interface EmailFacade {

    void sendVerificationCode(EmailDTO emailDTO);

    boolean verifyCode(VerifyCodeDTO verifyCodeDTO);

}
