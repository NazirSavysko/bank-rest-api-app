package bank.rest.app.bankrestapp.entity;

import bank.rest.app.bankrestapp.entity.enums.AccountStatus;
import bank.rest.app.bankrestapp.entity.enums.Concurrency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "account")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Account {

    @Id
    private Integer accountId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    private String accountNumber;

    private BigDecimal balance;

    @Enumerated(STRING)
    private Concurrency currencyCode;

    @Enumerated(STRING)
    private AccountStatus status;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Card card;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactionHistory;

    @OneToMany(mappedBy = "account")
    private List<Payment> paymentsList;
}
