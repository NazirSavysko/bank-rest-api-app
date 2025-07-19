package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "auth_user")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class AuthUSer {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Integer userId;

    private String email;

    private String passwordHash;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "authUser", cascade = ALL, fetch = EAGER)
    private Customer customer;

    @ManyToMany(cascade = {DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "customer_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<CustomerRole> customerRole;
}
