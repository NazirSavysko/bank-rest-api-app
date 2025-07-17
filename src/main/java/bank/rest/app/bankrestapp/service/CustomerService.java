package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Customer;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerService {

    Customer login(String email, String password);

    @Transactional(rollbackFor = Exception.class)
    Customer register(String firstName, String lastName, String email, String password, String phoneNumber);
}
