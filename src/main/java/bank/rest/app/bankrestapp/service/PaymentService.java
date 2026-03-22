package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.dto.IbanPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.InternetPaymentRequestDTO;
import bank.rest.app.bankrestapp.dto.MobilePaymentRequestDTO;
import bank.rest.app.bankrestapp.entity.Payment;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentService {

    /**
     * Processes an IBAN payment from the authenticated user's account.
     *
     * @param request IBAN payment request details
     * @param authenticatedUserEmail email of the authenticated user
     * @return persisted payment entity
     * @throws IllegalArgumentException if the account does not belong to the user or the IBAN is invalid
     * @throws IllegalStateException if a FOP account does not contain an EDRPOU code
     * @throws java.util.NoSuchElementException if the sender or recipient account cannot be found
     */
    @Transactional(rollbackFor = Exception.class)
    Payment processIbanPayment(IbanPaymentRequestDTO request, String authenticatedUserEmail);

    /**
     * Processes an internet-service payment from the authenticated user's account.
     *
     * @param request internet payment request details
     * @param authenticatedUserEmail email of the authenticated user
     * @return persisted payment entity
     * @throws IllegalArgumentException if the account does not belong to the user
     * @throws java.util.NoSuchElementException if the account cannot be found
     */
    @Transactional(rollbackFor = Exception.class)
    Payment processInternetPayment(InternetPaymentRequestDTO request, String authenticatedUserEmail);

    /**
     * Processes a mobile top-up payment from the authenticated user's account.
     *
     * @param request mobile payment request details
     * @param authenticatedUserEmail email of the authenticated user
     * @return persisted payment entity
     * @throws IllegalArgumentException if the account does not belong to the user
     * @throws java.util.NoSuchElementException if the account cannot be found
     */
    @Transactional(rollbackFor = Exception.class)
    Payment processMobilePayment(MobilePaymentRequestDTO request, String authenticatedUserEmail);
}
