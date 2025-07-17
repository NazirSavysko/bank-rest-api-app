package bank.rest.app.bankrestapp.resository;

import bank.rest.app.bankrestapp.entity.CustomerRole;
import bank.rest.app.bankrestapp.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRoleRepository extends JpaRepository<CustomerRole, Integer> {
    Optional<CustomerRole> findByRoleName(Role role);
}
