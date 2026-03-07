package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.entity.Payment;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentService {

    @Transactional
    Payment processIbanPayment(IbanPaymentRequestDTO request, String authenticatedUserEmail);

    @Transactional
    Payment processInternetPayment(InternetPaymentRequestDTO request, String authenticatedUserEmail);
}
