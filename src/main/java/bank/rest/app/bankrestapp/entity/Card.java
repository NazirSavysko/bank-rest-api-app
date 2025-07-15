package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "card")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Card {

    @Id
    private Integer cardId;

    private String cardNumber;

    private LocalDateTime expiryDate;

    private String cvv;

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private Account account;
}
