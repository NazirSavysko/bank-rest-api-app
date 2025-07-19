package bank.rest.app.bankrestapp.entity;

import bank.rest.app.bankrestapp.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "role")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRole {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Integer id;

    @Enumerated(value = STRING)
    private Role roleName;

    @ManyToMany(mappedBy = "customerRole", cascade = {DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<AuthUSer> authUsers;
}
