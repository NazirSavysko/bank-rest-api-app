package bank.rest.app.bankrestapp.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_codes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationCodes {

    @Id
    private Integer id;

    private String email;

    private String verificationCode;

    private LocalDateTime createdAt;
}
