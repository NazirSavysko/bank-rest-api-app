package bank.rest.app.bankrestapp.entity;

import bank.rest.app.bankrestapp.entity.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

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

    private Role role;
}
