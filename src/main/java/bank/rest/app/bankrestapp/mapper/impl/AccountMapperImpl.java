package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetCardDTO;
import bank.rest.app.bankrestapp.dto.get.GetPaymentDTO;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static bank.rest.app.bankrestapp.utils.MapperUtils.mapCollection;
import static bank.rest.app.bankrestapp.utils.MapperUtils.mapDto;

/**
 * Implementation of the Mapper interface for converting Account entities to GetAccountDTO objects.
 *
 * <p>This mapper handles the transformation of Account domain objects into Data Transfer Objects
 * suitable for API responses. It includes nested mapping of related entities such as cards,
 * transaction history, and payment lists.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Maps basic account information (number, balance, currency, status)</li>
 *   <li>Converts enum values to string representations for JSON serialization</li>
 *   <li>Handles nested entity mapping using injected mappers</li>
 *   <li>Transforms collections of related entities (transactions, payments)</li>
 * </ul>
 *
 * <p>Example transformation:</p>
 * <pre>
 * Account entity with:
 * - accountNumber: "1234567890"
 * - balance: 1500.50
 * - currencyCode: Currency.USD
 * - status: AccountStatus.ACTIVE
 * - card: Card object
 * - transactionHistory: List of Transaction objects
 * - paymentsList: List of Payment objects
 *
 * Becomes GetAccountDTO with:
 * - accountNumber: "1234567890"
 * - balance: 1500.50
 * - currencyCode: "USD"
 * - status: "ACTIVE"
 * - card: GetCardDTO object
 * - transactionHistory: List of GetTransactionDTO objects
 * - paymentsList: List of GetPaymentDTO objects
 * </pre>
 *
 * @see bank.rest.app.bankrestapp.entity.Account
 * @see bank.rest.app.bankrestapp.dto.get.GetAccountDTO
 * @see bank.rest.app.bankrestapp.mapper.Mapper
 *
 * @author Nazira Savisska
 * @since 1.0
 */
@Component
public final class AccountMapperImpl implements Mapper<Account, GetAccountDTO> {

    private final Mapper<Card, GetCardDTO> cardMapper;
    private final Mapper<Transaction, GetTransactionDTO> transactionMapper;
    private final Mapper<Payment, GetPaymentDTO> paymentMapper;

    /**
     * Constructs an AccountMapperImpl with the required nested mappers.
     *
     * @param cardMapper        mapper for converting Card entities to GetCardDTO
     * @param transactionMapper mapper for converting Transaction entities to GetTransactionDTO
     * @param paymentMapper     mapper for converting Payment entities to GetPaymentDTO
     */
    @Autowired
    public AccountMapperImpl(final Mapper<Card, GetCardDTO> cardMapper,
                             final Mapper<Transaction, GetTransactionDTO> transactionMapper,
                             final Mapper<Payment, GetPaymentDTO> paymentMapper) {
        this.cardMapper = cardMapper;
        this.transactionMapper = transactionMapper;
        this.paymentMapper = paymentMapper;
    }

    /**
     * Converts an Account entity to a GetAccountDTO.
     *
     * <p>This method performs a comprehensive mapping that includes:</p>
     * <ul>
     *   <li>Basic account properties (number, balance)</li>
     *   <li>Enum to string conversion for currency and status</li>
     *   <li>Nested card mapping using the injected card mapper</li>
     *   <li>Collection mapping for transaction history and payments</li>
     * </ul>
     *
     * @param entity the Account entity to convert; must not be null
     * @return a new GetAccountDTO containing the mapped data
     * @throws IllegalArgumentException if the entity is null
     */
    @Contract("_ -> new")
    @Override
    public @NotNull GetAccountDTO toDto(final @NotNull Account entity) {
        return new GetAccountDTO(
                entity.getAccountNumber(),
                entity.getBalance(),
                entity.getCurrencyCode().name(),
                entity.getStatus().name(),
                mapDto(entity.getCard(),this.cardMapper::toDto),
                mapCollection(entity.getTransactionHistory(), this.transactionMapper::toDto),
                mapCollection(entity.getPaymentsList(), this.paymentMapper::toDto)
        ) ;
    }
}
