package bank.rest.app.bankrestapp.entity;

import bank.rest.app.bankrestapp.entity.enums.AccountStatus;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "account")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Account {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Integer accountId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    private String accountNumber;

    private BigDecimal balance;

    @Enumerated(STRING)
    private Currency currencyCode;

    @Enumerated(STRING)
    private AccountStatus status;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "account", cascade = ALL)
    private Card card;

    @OneToMany(mappedBy = "account", cascade = ALL)
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "toAccount", cascade = ALL)
    private List<Transaction> receivedTransactions;

    @OneToMany(mappedBy = "account", cascade = ALL)
    private List<Payment> paymentsList;
}
