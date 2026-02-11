package bank.rest.app.bankrestapp.entity;

import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "transaction")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Transaction {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Integer transactionId;

    private BigDecimal amount;

    @Enumerated(STRING)
    private Currency currencyCode;

    private String description;

    @Enumerated(STRING)
    private TransactionType transactionType;

    @Enumerated(STRING)
    private TransactionStatus status;

    private LocalDateTime transactionDate;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "from_account_id", referencedColumnName = "accountId")
    private Account account;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "to_account_id", referencedColumnName = "accountId")
    private Account toAccount;

    @Transient
    private Boolean isRecipient = Boolean.FALSE;
}

