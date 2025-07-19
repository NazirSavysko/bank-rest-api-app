package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "customer")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Customer {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Integer customer_id;

    private String lastName;

    private String firstName;

    private String phone;

    private LocalDateTime createdAt;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private AuthUSer authUser;


    @OneToMany(mappedBy = "customer", cascade = ALL)
    private java.util.List<Account> accounts;

}
