package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "card")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Card {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Integer cardId;

    private String cardNumber;

    private LocalDateTime expiryDate;

    private String cvv;

    private LocalDateTime createdAt;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private Account account;
}
