package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.resository.CustomerRepository;
import bank.rest.app.bankrestapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}
