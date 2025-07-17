package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.ResetPasswordRequestDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;
import bank.rest.app.bankrestapp.facade.EmailFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/v1/verify")
public final class EmailVerificationController {

    private final EmailFacade emailFacade;

    @Autowired
    public EmailVerificationController(final EmailFacade emailFacade) {
        this.emailFacade = emailFacade;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(final @RequestParam EmailDTO emailDTO) {
        emailFacade.sendVerificationCode(emailDTO);

        return ResponseEntity
                .ok("Code has been sent to your email");
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkCode(final @RequestParam VerifyCodeDTO verifyCodeDTO) {
        final boolean valid = emailFacade.verifyCode(verifyCodeDTO);

        return valid ? ResponseEntity.ok("Code is valid") :
                ResponseEntity
                        .status(BAD_REQUEST)
                        .body("Invalid code");
    }
}
