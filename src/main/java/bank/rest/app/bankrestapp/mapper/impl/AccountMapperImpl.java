package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
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

import java.util.List;
import java.util.stream.Stream;

import static bank.rest.app.bankrestapp.utils.MapperUtils.mapCollection;
import static bank.rest.app.bankrestapp.utils.MapperUtils.mapDto;
import static java.util.stream.Stream.concat;

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
 * @author Nazira Savisska
 * @see bank.rest.app.bankrestapp.entity.Account
 * @see bank.rest.app.bankrestapp.dto.get.GetAccountDTO
 * @see bank.rest.app.bankrestapp.mapper.Mapper
 * @since 1.0
 */
@Component
public final class AccountMapperImpl implements Mapper<Account, GetAccountDTO> {

    private final Mapper<Card, GetCardDTO> cardMapper;
    private final Mapper<Transaction, GetTransactionDTO> transactionMapper;
    private final Mapper<Payment, GetPaymentDTO> paymentMapper;
    private final CurrencyLoader currencyLoader;

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
                             final CurrencyLoader currencyLoader,
                             final Mapper<Payment, GetPaymentDTO> paymentMapper) {
        this.cardMapper = cardMapper;
        this.transactionMapper = transactionMapper;
        this.paymentMapper = paymentMapper;
        this.currencyLoader = currencyLoader;
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
                mapDto(entity.getCard(), this.cardMapper::toDto),
                mapCollection(this.getTransactionHistory(entity.getSentTransactions(), entity.getReceivedTransactions(), entity), this.transactionMapper::toDto),
                mapCollection(entity.getPaymentsList(), this.paymentMapper::toDto)
        );
    }

    /**
     * Merges the sender's and recipient's transaction histories into a single list.
     *
     * <p>This method ensures that all transactions are converted to the account's currency
     * and combines both lists into one, maintaining the order of sender transactions first.</p>
     *
     * @param senderTransactions    the list of transactions sent by the account; must not be null
     * @param recipientTransactions the list of transactions received by the account; must not be null
     * @param account               the account for which the transaction history is being retrieved; must not be null
     * @return a merged list of transactions with amounts converted to the account's currency
     */
    private @NotNull List<Transaction> getTransactionHistory(final @NotNull List<Transaction> senderTransactions, final @NotNull List<Transaction> recipientTransactions, final Account account) {

        if (senderTransactions.isEmpty() && recipientTransactions.isEmpty()) {
            return List.of();
        }

        if (recipientTransactions.isEmpty()) {
            return senderTransactions;
        }
        if (senderTransactions.isEmpty()) {
            return recipientTransactions;
        }

        final Stream<Transaction> getStream = recipientTransactions.stream().peek(transaction -> {
            transaction.setAmount(currencyLoader.convert(transaction.getAmount(), transaction.getCurrencyCode().name(), account.getCurrencyCode().name()));
            transaction.setCurrencyCode(account.getCurrencyCode());
            transaction.setIsRecipient(true);
        });

        return concat(getStream, senderTransactions.stream()).toList();

    }
}
