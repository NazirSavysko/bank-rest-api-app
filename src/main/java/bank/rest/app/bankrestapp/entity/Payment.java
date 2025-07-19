package bank.rest.app.bankrestapp.entity;

import bank.rest.app.bankrestapp.entity.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "payment")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Payment {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Integer paymentId;

    private BigDecimal amount;

    @Enumerated(STRING)
    private Currency currencyCode;

    private String BeneficiaryName;

    private String BeneficiaryAcc;

    private String purpose;

    private LocalDateTime paymentDate;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private Account account;
}
