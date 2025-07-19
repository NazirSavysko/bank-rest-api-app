package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;

public interface EmailFacade {

    void sendVerificationCode(EmailDTO email);

    boolean verifyCode(VerifyCodeDTO verifyCodeDTO);

}
