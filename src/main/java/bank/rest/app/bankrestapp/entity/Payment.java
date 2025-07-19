package bank.rest.app.bankrestapp.entity;

import bank.rest.app.bankrestapp.entity.enums.Concurrency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "payment")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Payment {

    @Id
    private Integer paymentId;

    private BigDecimal amount;

    @Enumerated(STRING)
    private Concurrency currencyCode;

    private String BeneficiaryName;

    private String BeneficiaryAcc;

    private String purpose;

    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private Account account;
}
