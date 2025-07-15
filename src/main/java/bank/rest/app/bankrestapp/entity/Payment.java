package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private String currencyCode;

    private String BeneficiaryName;

    private String BeneficiaryAcc;

    private String purpose;

    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private Account account;
}
