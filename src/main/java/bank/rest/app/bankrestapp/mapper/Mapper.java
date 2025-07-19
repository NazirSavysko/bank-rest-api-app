package bank.rest.app.bankrestapp.mapper;

/**
 * Functional interface for converting objects from one type to another.
 * Designed for converting entities to DTO objects and vice versa.
 * 
 * <p>This interface follows the Mapper pattern and provides a uniform
 * way to transform data between different application layers in the banking application.</p>
 *
 * <p>Real usage examples from the banking system:</p>
 * <pre>{@code
 * // Example 1: Converting Customer entity to CustomerDTO for API responses
 * Mapper<Customer, CustomerDTO> customerMapper = customer ->
 *     new CustomerDTO(
 *         mapCollection(customer.getAccounts(), accountMapper::toDto),
 *         customer.getFirstName(),
 *         customer.getLastName(),
 *         customer.getAuthUser().getEmail(),
 *         customer.getPhone()
 *     );
 *
 * // Example 2: Converting Account entity to AccountDTO with nested mappings
 * Mapper<Account, AccountDTO> accountMapper = account ->
 *     new AccountDTO(
 *         account.getAccountNumber(),
 *         account.getBalance(),
 *         account.getCurrencyCode().name(), // UAH, USD, EUR
 *         account.getStatus().name(), // ACTIVE, BLOCKED, CLOSED, etc.
 *         mapDto(account.getCard(), cardMapper::toDto),
 *         mapCollection(account.getTransactionHistory(), transactionMapper::toDto),
 *         mapCollection(account.getPaymentsList(), paymentMapper::toDto)
 *     );
 *
 * // Example 3: Converting Card entity to CardDTO (security-aware)
 * Mapper<Card, CardDTO> cardMapper = card ->
 *     new CardDTO(
 *         card.getCardNumber(),
 *         card.getExpiryDate(),
 *         "***" // CVV never exposed in API responses for security
 *     );
 *
 * // Example 4: Converting CreateCustomerDTO to Customer entity for persistence
 * Mapper<CreateCustomerDTO, Customer> createCustomerMapper = dto ->
 *     Customer.builder()
 *         .firstName(dto.firstName())
 *         .lastName(dto.lastName())
 *         .phone(dto.phoneNumber())
 *         .createdAt(LocalDateTime.now())
 *         .authUser(AuthUSer.builder()
 *             .email(dto.email())
 *             .passwordHash(hashPassword(dto.password())) // Never store plain passwords
 *             .createdAt(LocalDateTime.now())
 *             .build())
 *         .build();
 * 
 * // Usage in service layer with dependency injection
 * @Component
 * public class BankingService {
 *     private final Mapper<Customer, CustomerDTO> customerMapper;
 *
 *     public CustomerDTO getCustomerProfile(Integer customerId) {
 *         Customer entity = customerRepository.findById(customerId);
 *         return customerMapper.toDto(entity);
 *     }
 * }
 * }</pre>
 * 
 * @param <T> source object type (Entity: Customer, Account, Card, Payment, Transaction, AuthUSer)
 * @param <R> target object type (DTO: CustomerDTO, AccountDTO, CardDTO, PaymentDTO, TransactionDTO)
 *
 * @author Savysko Nazir
 * @version 1.0
 * @since 1.0
 * @see bank.rest.app.bankrestapp.entity.Customer
 * @see bank.rest.app.bankrestapp.entity.Account
 * @see bank.rest.app.bankrestapp.entity.Card
 * @see bank.rest.app.bankrestapp.dto.CustomerDTO
 * @see bank.rest.app.bankrestapp.dto.AccountDTO
 * @see bank.rest.app.bankrestapp.dto.CardDTO
 * @see bank.rest.app.bankrestapp.mapper.impl.CustomerMapperImpl
 * @see bank.rest.app.bankrestapp.mapper.impl.AccountMapperImpl
 * @see bank.rest.app.bankrestapp.mapper.impl.CardMapperImpl
 * @see bank.rest.app.utils.MapperUtils
 */
@FunctionalInterface
public interface Mapper<T,R>  {

    /**
     * Converts an object of type T to an object of type R.
     * 
     * <p>This method performs the main mapping logic, transforming
     * the source object into the target type. In the banking application context,
     * this is typically used for:</p>
     * <ul>
     *   <li>Converting Customer entities to CustomerDTO for API responses</li>
     *   <li>Converting Account entities to AccountDTO with nested Card and Transaction data</li>
     *   <li>Converting Card entities to CardDTO while protecting sensitive CVV information</li>
     *   <li>Converting incoming DTOs to entities for database persistence</li>
     *   <li>Transforming between different representation layers with proper enum handling</li>
     * </ul>
     *
     * <p><strong>Security considerations:</strong></p>
     * <ul>
     *   <li>Never expose sensitive data like CVV codes or plain text passwords in DTOs</li>
     *   <li>Use enum.name() for converting AccountStatus and Concurrency enums to strings</li>
     *   <li>Utilize {@link bank.rest.app.utils.MapperUtils} for null-safe collection mapping</li>
     *   <li>Handle nested object mappings with dependency injection in Spring components</li>
     * </ul>
     *
     * @param entity source object for conversion. Must not be null
     * @return converted object of type R, never null
     * @throws NullPointerException if entity is null (handled by MapperUtils.mapDto)
     * @throws IllegalArgumentException if entity contains invalid data for mapping
     *
     * @implNote Implementations should:
     *           <ul>
     *             <li>Use {@code @Component} annotation for Spring dependency injection</li>
     *             <li>Inject other mappers via constructor for nested object conversion</li>
     *             <li>Use {@code @Contract("_ -> new")} and {@code @NotNull} annotations</li>
     *             <li>Handle collections with {@link bank.rest.app.utils.MapperUtils#mapCollection}</li>
     *             <li>Handle single objects with {@link bank.rest.app.utils.MapperUtils#mapDto}</li>
     *           </ul>
     */
    R toDto(T entity);
}
