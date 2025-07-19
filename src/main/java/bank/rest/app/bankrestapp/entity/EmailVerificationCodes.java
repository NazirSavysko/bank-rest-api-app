package bank.rest.app.bankrestapp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "email_verification_codes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationCodes {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Integer id;

    private String email;

    private String code;

    private boolean isVerified;

    private LocalDateTime createdAt;
}
