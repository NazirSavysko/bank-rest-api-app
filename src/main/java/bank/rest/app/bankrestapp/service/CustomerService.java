package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerService {

    Customer login(String email);

    @Transactional(rollbackFor = Exception.class)
    void register(String firstName, String lastName, String email, String password, String phoneNumber);

    Customer checkIfAuthenticated(String email, String password);

    @Transactional(rollbackFor = Exception.class)
    void resetPassword(String email, String password);

    @Transactional(rollbackFor = Exception.class)
    void updatePassword(String email, String newPassword,String oldPassword);
}
