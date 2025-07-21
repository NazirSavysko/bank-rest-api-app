package bank.rest.app.bankrestapp.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.List.of;

/**
 * Utility class providing helper methods for object mapping operations.
 * This class contains static methods to facilitate conversion between different object types,
 * particularly useful for mapping entities to DTOs and vice versa in the banking application.
 *
 * <p>The class is designed to work seamlessly with the {@link bank.rest.app.bankrestapp.mapper.Mapper}
 * functional interface and provides null-safe operations for both single objects and collections.</p>
 *
 * <p>Real usage examples in the banking system:</p>
 * <pre>{@code
 * // Example 1: Mapping collection of Account entities to GetAccountDTO list
 * List<Account> accounts = customer.getAccounts();
 * List<GetAccountDTO> accountDTOs = MapperUtils.mapCollection(accounts, accountMapper::toDto);
 *
 * // Example 2: Mapping single Card entity to GetCardDTO
 * Card cardEntity = account.getCard();
 * GetCardDTO cardDTO = MapperUtils.mapDto(cardEntity, cardMapper::toDto);
 *
 * // Example 3: Mapping transaction history with null safety
 * List<Transaction> transactions = account.getTransactionHistory();
 * List<GetTransactionDTO> transactionDTOs = MapperUtils.mapCollection(transactions,
 *     transaction -> new GetTransactionDTO(
 *         transaction.getId(),
 *         transaction.getAmount(),
 *         transaction.getType().name(),
 *         transaction.getTimestamp()
 *     ));
 *
 * // Example 4: Chaining with optional operations
 * Optional<Customer> customerOpt = customerRepository.findById(id);
 * GetCustomerDTO customerDTO = customerOpt
 *     .map(customer -> MapperUtils.mapDto(customer, customerMapper::toDto))
 *     .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
 * }</pre>
 *
 * @author Savysko Nazir
 * @version 1.0
 * @since 1.0
 * @see bank.rest.app.bankrestapp.mapper.Mapper
 * @see bank.rest.app.bankrestapp.mapper.impl.CustomerMapperImpl
 * @see bank.rest.app.bankrestapp.mapper.impl.AccountMapperImpl
 * @see bank.rest.app.bankrestapp.mapper.impl.CardMapperImpl
 */
public final class MapperUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class is designed to be used only through its static methods.
     */
    private MapperUtils() {
        // Utility class - no instantiation allowed
    }

    /**
     * Maps a collection of objects from type T to a list of objects of type R.
     * This method provides null-safe collection mapping and handles empty collections gracefully.
     *
     * <p>The method uses Java streams for efficient processing and returns an immutable list
     * created with {@link List#of()} for empty collections, ensuring thread safety.</p>
     *
     * <p><strong>Banking application use cases:</strong></p>
     * <ul>
     *   <li>Converting Customer's accounts list to GetAccountDTO list for API responses</li>
     *   <li>Converting Account's transaction history to GetTransactionDTO list</li>
     *   <li>Converting Account's payments list to GetPaymentDTO list</li>
     *   <li>Converting any entity collections to corresponding DTO collections</li>
     * </ul>
     *
     * @param <T> the type of objects in the source collection (usually Entity types)
     * @param <R> the type of objects in the resulting list (usually DTO types)
     * @param source the collection to be mapped. Can be null or empty
     * @param mapper the function that converts each element from type T to type R.
     *               Must not be null and should not return null for valid inputs
     * @return an immutable list containing the mapped objects.
     *         Returns empty list if source is null or empty.
     *         Never returns null.
     *
     * @throws NullPointerException if mapper is null
     * @throws RuntimeException if mapper throws an exception during conversion
     *
     * @implNote This method:
     *           <ul>
     *             <li>Returns {@link List#of()} for null or empty collections (immutable empty list)</li>
     *             <li>Uses {@link Collection#stream()} for efficient processing</li>
     *             <li>Uses {@link java.util.stream.Stream#toList()} which returns an immutable list in Java 16+</li>
     *             <li>Preserves the order of elements from the source collection</li>
     *           </ul>
     *
     * @see #mapDto(Object, Function)
     * @see java.util.stream.Stream#map(Function)
     * @see java.util.stream.Stream#toList()
     */
    public static <T, R> List<R> mapCollection(Collection<T> source, Function<T, R> mapper) {
        if (source == null || source.isEmpty()) {
            return of();
        }
        return source.stream()
                .map(mapper)
                .toList();
    }

    /**
     * Maps a single object from type T to type R using the provided mapper function.
     * This method provides null-safe object mapping with explicit null checking.
     *
     * <p>Unlike collection mapping, this method throws an exception for null sources
     * to ensure data integrity and prevent silent failures in the banking application
     * where null entities could indicate serious data inconsistency.</p>
     *
     * <p><strong>Banking application use cases:</strong></p>
     * <ul>
     *   <li>Converting Customer entity to GetCustomerDTO for profile responses</li>
     *   <li>Converting Account entity to GetAccountDTO for account details</li>
     *   <li>Converting Card entity to GetCardDTO for card information (with security masking)</li>
     *   <li>Converting AuthUser entity to UserDTO for authentication responses</li>
     * </ul>
     *
     * @param <T> the type of the source object (usually Entity type)
     * @param <R> the type of the resulting object (usually DTO type)
     * @param source the object to be mapped. Must not be null
     * @param mapper the function that converts the object from type T to type R.
     *               Must not be null and should not return null for valid inputs
     * @return the mapped object of type R. Never returns null for valid inputs.
     *
     * @throws NullPointerException if source is null (with descriptive message)
     * @throws NullPointerException if mapper is null
     * @throws RuntimeException if mapper throws an exception during conversion
     *
     * @implNote This method:
     *           <ul>
     *             <li>Performs explicit null check on source with meaningful error message</li>
     *             <li>Directly applies the mapper function without additional processing</li>
     *             <li>Fails fast on null inputs to prevent data corruption</li>
     *             <li>Preserves any exceptions thrown by the mapper function</li>
     *           </ul>
     *
     * @see #mapCollection(Collection, Function)
     * @see java.util.function.Function#apply(Object)
     */
    public static <T, R> R mapDto(T source, Function<T, R> mapper) {
        if (source == null) {
            throw new NullPointerException("Cannot map null source object");
        }
        return mapper.apply(source);
    }
}
