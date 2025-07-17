package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name = "auth_user")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class AuthUSer {

    @Id
    private Integer userId;

    private String email;

    private String passwordHash;

    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "customer_roles",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<CustomerRole> customerRole;
}
