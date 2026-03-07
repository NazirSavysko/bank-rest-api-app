package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetShortCustomerDTO;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.TransactionType;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

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
 *   <li>Handles both sender and receiver customer information (receiver may be null for internet payments)</li>
 *   <li>Transforms LocalDateTime to string for API response</li>
 *   <li>Computes optional {@code paymentSubtype} discriminator for frontend display logic</li>
 * </ul>
 *
 * <p>Computed {@code paymentSubtype} values:</p>
 * <ul>
 *   <li>{@code "IBAN_PAYMENT"} – outgoing IBAN transfer (type=IBAN_PAYMENT, isRecipient=false)</li>
 *   <li>{@code "IBAN_RECEIPT"} – incoming IBAN transfer (type=IBAN_PAYMENT, isRecipient=true)</li>
 *   <li>{@code "INTERNET_PAYMENT"} – internet/utility/mobile service payment (type=PAYMENT)</li>
 *   <li>{@code null} – regular card-to-card transfer (type=TRANSFER)</li>
 * </ul>
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

    @Contract("_ -> new")
    @Override
    public @NotNull GetTransactionDTO toDto(final @NotNull Transaction entity) {
        final Mapper<Customer, GetShortCustomerDTO> customerMapper =
                (customer) -> new GetShortCustomerDTO(
                        customer.getFirstName(),
                        customer.getLastName()
                );

        final Account toAccount = entity.getToAccount();
        final GetShortCustomerDTO receiverDto = toAccount != null
                ? mapDto(toAccount.getCustomer(), customerMapper::toDto)
                : null;
        final String receiverCardNumber = (toAccount != null && toAccount.getCard() != null)
                ? toAccount.getCard().getCardNumber()
                : null;

        return new GetTransactionDTO(
                mapDto(entity.getAccount().getCustomer(), customerMapper::toDto),
                receiverDto,
                entity.getAmount(),
                computeDisplayDescription(entity),
                entity.getTransactionDate().toString(),
                entity.getTransactionType().name(),
                entity.getCurrencyCode().name(),
                entity.getStatus().name(),
                entity.getAccount().getCard() != null ? entity.getAccount().getCard().getCardNumber() : null,
                entity.getIsRecipient(),
                receiverCardNumber,
                computePaymentSubtype(entity)
        );
    }

    /**
     * Computes the user-facing description for a transaction.
     *
     * <p>For an IBAN payment seen by the recipient ({@code isRecipient=true}), the stored description
     * contains the sender's outgoing wording ("Платіж по IBAN: …"). This method replaces it with
     * the incoming wording ("Зарахування по IBAN: {senderName}") so that each party sees a
     * direction-appropriate label.</p>
     *
     * @param entity the transaction entity; must not be null
     * @return display description appropriate for the viewing party
     */
    @NotNull
    private static String computeDisplayDescription(final @NotNull Transaction entity) {
        if (entity.getTransactionType() == TransactionType.IBAN_PAYMENT
                && Boolean.TRUE.equals(entity.getIsRecipient())) {
            final Account senderAccount = entity.getAccount();
            if (senderAccount != null && senderAccount.getCustomer() != null) {
                final Customer sender = senderAccount.getCustomer();
                final String senderName = java.util.stream.Stream.of(sender.getFirstName(), sender.getLastName())
                        .filter(s -> s != null && !s.isBlank())
                        .collect(java.util.stream.Collectors.joining(" "));
                return "Зарахування по IBAN: " + senderName;
            }
        }
        return entity.getDescription() != null ? entity.getDescription() : "";
    }

    @Nullable
    private static String computePaymentSubtype(final @NotNull Transaction entity) {
        return switch (entity.getTransactionType()) {
            case IBAN_PAYMENT -> Boolean.TRUE.equals(entity.getIsRecipient()) ? "IBAN_RECEIPT" : "IBAN_PAYMENT";
            case PAYMENT -> "INTERNET_PAYMENT";
            default -> null;
        };
    }
}

