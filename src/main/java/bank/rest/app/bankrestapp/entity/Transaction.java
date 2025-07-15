package bank.rest.app.bankrestapp.entity;

import bank.rest.app.bankrestapp.entity.enums.Concurrency;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "transaction")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Transaction {

    @Id
    private Integer transactionId;

    private BigDecimal amount;

    private Concurrency currencyCode;

    private String description;

    @Enumerated(STRING)
    private TransactionType transactionType;

    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "from_account_id", referencedColumnName = "accountId")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "to_account_id", referencedColumnName = "accountId")
    private Account toAccount;

}

