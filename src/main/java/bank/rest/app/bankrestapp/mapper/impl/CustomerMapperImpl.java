package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetCustomerDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static bank.rest.app.bankrestapp.utils.MapperUtils.mapCollection;

/**
 * Implementation of the Mapper interface for converting Customer entities to GetCustomerDTO objects.
 *
 * <p>This mapper handles the transformation of Customer domain objects into Data Transfer Objects
 * suitable for API responses. It includes mapping of customer personal information and their
 * associated accounts collection.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Maps customer personal information (first name, last name, phone)</li>
 *   <li>Extracts email from associated AuthUser entity</li>
 *   <li>Transforms the customer's accounts collection using injected account mapper</li>
 *   <li>Handles nested entity relationships efficiently</li>
 * </ul>
 *
 * <p>Example transformation:</p>
 * <pre>
 * Customer entity with:
 * - firstName: "John"
 * - lastName: "Doe"
 * - phone: "+1234567890"
 * - authUser.email: "john.doe@example.com"
 * - accounts: List of Account objects
 *
 * Becomes GetCustomerDTO with:
 * - accounts: List of GetAccountDTO objects
 * - firstName: "John"
 * - lastName: "Doe"
 * - email: "john.doe@example.com"
 * - phone: "+1234567890"
 * </pre>
 *
 * @see bank.rest.app.bankrestapp.entity.Customer
 * @see bank.rest.app.bankrestapp.dto.get.GetCustomerDTO
 * @see bank.rest.app.bankrestapp.mapper.Mapper
 *
 * @author Nazira Savisska
 * @since 1.0
 */
@Component
public final class CustomerMapperImpl implements Mapper<Customer, GetCustomerDTO> {

    private final Mapper<Account, GetAccountDTO> accountMapper;

    /**
     * Constructs a CustomerMapperImpl with the required account mapper.
     *
     * @param accountMapper mapper for converting Account entities to GetAccountDTO objects
     */
    @Autowired
    public CustomerMapperImpl(final Mapper<Account, GetAccountDTO> accountMapper) {
        this.accountMapper = accountMapper;
    }

    /**
     * Converts a Customer entity to a GetCustomerDTO.
     *
     * <p>This method performs comprehensive mapping that includes:</p>
     * <ul>
     *   <li>Customer personal information extraction</li>
     *   <li>Email retrieval from the associated AuthUser entity</li>
     *   <li>Collection mapping of all customer accounts</li>
     * </ul>
     *
     * @param entity the Customer entity to convert; must not be null
     * @return a new GetCustomerDTO containing the mapped customer data
     * @throws IllegalArgumentException if the entity is null
     */
    @Contract("_ -> new")
    @Override
    public @NotNull GetCustomerDTO toDto(final @NotNull Customer entity) {
        return new GetCustomerDTO(
                mapCollection(entity.getAccounts(), this.accountMapper::toDto),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAuthUser().getEmail(),
                entity.getPhone()
        );
    }
}
