package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Customer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerService {

    @Transactional(readOnly = true)
    Customer login(String email);

    @Transactional(rollbackFor = Exception.class)
    void register(String firstName, String lastName, String email, String password, String phoneNumber);

    @Transactional(readOnly = true)
    Customer checkIfAuthenticated(String email, String password);

    @Transactional(rollbackFor = Exception.class)
    void resetPassword(String email, String password);

    @Transactional(rollbackFor = Exception.class)
    void updatePassword(String email, String newPassword,String oldPassword);

    @Transactional(readOnly = true)
    List<Customer> getAllCustomers();
}
