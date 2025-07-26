package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.get.GetShortCustomerDTO;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static bank.rest.app.bankrestapp.utils.MapperUtils.mapDto;

/**
 * Implementation of the Mapper interface for converting Transaction entities to GetTransactionDTO objects.
 *
 * <p>This mapper handles the transformation of Transaction domain objects into Data Transfer Objects
 * suitable for API responses. It includes mapping of transaction details, involved customers,
 * and converts enums to string representations for JSON serialization.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Maps transaction financial details (amount, currency, date)</li>
 *   <li>Converts enum values (transaction type, status, currency) to strings</li>
 *   <li>Creates inline customer mapper for GetShortCustomerDTO transformation</li>
 *   <li>Handles both sender and receiver customer information</li>
 *   <li>Transforms LocalDateTime to string for API response</li>
 * </ul>
 *
 * <p>Design Note: This mapper creates an inline customer mapper instead of injecting it.
 * This approach reduces dependencies but could be extracted to a separate component
 * for better reusability and testability.</p>
 *
 * <p>Example transformation:</p>
 * <pre>
 * Transaction entity with:
 * - account.customer: Customer("John", "Doe")
 * - toAccount.customer: Customer("Jane", "Smith")
 * - amount: BigDecimal("500.00")
 * - description: "Monthly transfer"
 * - transactionDate: LocalDateTime.now()
 * - transactionType: TransactionType.TRANSFER
 * - currencyCode: Currency.USD
 * - status: TransactionStatus.COMPLETED
 *
 * Becomes GetTransactionDTO with:
 * - fromCustomer: GetShortCustomerDTO("John", "Doe")
 * - toCustomer: GetShortCustomerDTO("Jane", "Smith")
 * - amount: BigDecimal("500.00")
 * - description: "Monthly transfer"
 * - transactionDate: "2025-01-21T10:30:00"
 * - transactionType: "TRANSFER"
 * - currencyCode: "USD"
 * - status: "COMPLETED"
 * </pre>
 *
 * @author Nazira Savisska
 * @see bank.rest.app.bankrestapp.entity.Transaction
 * @see bank.rest.app.bankrestapp.dto.get.GetTransactionDTO
 * @see bank.rest.app.bankrestapp.dto.get.GetShortCustomerDTO
 * @see bank.rest.app.bankrestapp.mapper.Mapper
 * @since 1.0
 */
@Component
public final class TransactionMapperImpl implements Mapper<Transaction, GetTransactionDTO> {

    /**
     * Converts a Transaction entity to a GetTransactionDTO.
     *
     * <p>This method performs comprehensive mapping that includes:</p>
     * <ul>
     *   <li>Creation of an inline customer mapper for short customer information</li>
     *   <li>Mapping of both sender and receiver customer details</li>
     *   <li>Conversion of enum values to string representations</li>
     *   <li>DateTime formatting for API response</li>
     * </ul>
     *
     * <p>The inline customer mapper creates GetShortCustomerDTO objects containing
     * only essential customer information (first name and last name) to avoid
     * exposing sensitive data in transaction history.</p>
     *
     * @param entity the Transaction entity to convert; must not be null
     * @return a new GetTransactionDTO containing the mapped transaction data
     * @throws IllegalArgumentException if the entity is null
     */
    @Contract("_ -> new")
    @Override
    public @NotNull GetTransactionDTO toDto(final @NotNull Transaction entity) {
        final Mapper<Customer, GetShortCustomerDTO> customerMapper =
                (customer) -> new GetShortCustomerDTO(
                        customer.getFirstName(),
                        customer.getLastName()
                );

        return new GetTransactionDTO(
                mapDto(entity.getAccount().getCustomer(), customerMapper::toDto),
                mapDto(entity.getToAccount().getCustomer(), customerMapper::toDto),
                entity.getAmount(),
                entity.getDescription(),
                entity.getTransactionDate().toString(),
                entity.getTransactionType().name(),
                entity.getCurrencyCode().name(),
                entity.getStatus().name()
        );
    }
}
