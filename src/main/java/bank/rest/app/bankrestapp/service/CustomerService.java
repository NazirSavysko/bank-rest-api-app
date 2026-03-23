package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Customer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerService {

    /**
     * Loads a customer by email for authentication-related flows.
     *
     * @param email customer email
     * @return found customer entity
     * @throws java.util.NoSuchElementException if no customer exists with the specified email
     */
    @Transactional(readOnly = true)
    Customer login(String email);

    /**
     * Registers a new customer and creates the default account/card set for the profile.
     *
     * @param firstName customer first name
     * @param lastName customer last name
     * @param email customer email
     * @param password raw password to encode and store
     * @param phoneNumber customer phone number
     * @throws IllegalArgumentException if the email or phone number is already used
     * @throws java.util.NoSuchElementException if the default customer role cannot be found
     */
    @Transactional(rollbackFor = Exception.class)
    void register(String firstName, String lastName, String email, String password, String phoneNumber);

    /**
     * Verifies customer credentials and returns the authenticated customer.
     *
     * @param email customer email
     * @param password raw password supplied by the customer
     * @return authenticated customer entity
     * @throws IllegalArgumentException if the password is invalid
     * @throws java.util.NoSuchElementException if the customer does not exist
     */
    @Transactional(readOnly = true)
    Customer checkIfAuthenticated(String email, String password);

    /**
     * Resets the customer password after verification checks.
     *
     * @param email customer email
     * @param password new raw password
     * @throws IllegalArgumentException if the new password is invalid
     * @throws java.util.NoSuchElementException if the customer does not exist
     */
    @Transactional(rollbackFor = Exception.class)
    void resetPassword(String email, String password);

    /**
     * Changes the customer password after validating the current password.
     *
     * @param email customer email
     * @param newPassword new raw password
     * @param oldPassword current raw password
     * @throws IllegalArgumentException if the new or current password is invalid
     * @throws java.util.NoSuchElementException if the customer does not exist
     */
    @Transactional(rollbackFor = Exception.class)
    void updatePassword(String email, String newPassword,String oldPassword);

    /**
     * Sends a verification code to the authenticated customer for password change.
     *
     * @param email authenticated customer email
     */
    @Transactional(rollbackFor = Exception.class)
    void initPasswordChange(String email);

    /**
     * Changes the authenticated customer's password after OTP verification.
     *
     * @param email authenticated customer email
     * @param verificationCode otp verification code
     * @param newPassword new raw password
     */
    @Transactional(rollbackFor = Exception.class)
    void changePassword(String email, String verificationCode, String newPassword);

    /**
     * Sends a verification code to the authenticated customer for email change.
     *
     * @param email authenticated customer email
     */
    @Transactional(rollbackFor = Exception.class)
    void initEmailChange(String email);

    /**
     * Changes the authenticated customer's email after OTP verification.
     *
     * @param email authenticated customer email
     * @param verificationCode otp verification code
     * @param newEmail new email value
     */
    @Transactional(rollbackFor = Exception.class)
    void changeEmail(String email, String verificationCode, String newEmail);

    /**
     * Returns all customers for administrative use cases.
     *
     * @return list of all customers
     */
    @Transactional(readOnly = true)
    List<Customer> getAllCustomers();
}
