package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetPaymentDTO;
import bank.rest.app.bankrestapp.entity.IbanPayment;
import bank.rest.app.bankrestapp.entity.InternetPayment;
import bank.rest.app.bankrestapp.entity.MobilePayment;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.UtilityPayment;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Mapper interface for converting Payment entities to GetPaymentDTO objects.
 *
 * <p>This mapper handles the transformation of Payment domain objects into Data Transfer Objects
 * suitable for API responses. It converts payment information including currency, amount,
 * beneficiary details, and payment purpose.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Converts currency enum to string representation</li>
 *   <li>Transforms BigDecimal amount to string for JSON serialization</li>
 *   <li>Maps beneficiary name and payment purpose directly</li>
 *   <li>Handles financial data formatting appropriately</li>
 * </ul>
 *
 * <p>Note: This class has generic type parameters {@code <T, R>} which appear to be unused
 * in the current implementation. Consider removing them for cleaner code.</p>
 *
 * <p>Example transformation:</p>
 * <pre>
 * Payment entity with:
 * - currencyCode: Currency.USD
 * - amount: BigDecimal("1000.50")
 * - beneficiaryName: "John Smith"
 * - purpose: "Invoice payment #12345"
 *
 * Becomes GetPaymentDTO with:
 * - currencyCode: "USD"
 * - amount: "1000.50"
 * - beneficiaryName: "John Smith"
 * - purpose: "Invoice payment #12345"
 * </pre>
 *
 * @see bank.rest.app.bankrestapp.entity.Payment
 * @see bank.rest.app.bankrestapp.dto.get.GetPaymentDTO
 * @see bank.rest.app.bankrestapp.mapper.Mapper
 *
 * @author Nazira Savisska
 * @since 1.0
 */
@Component
public final class PaymantMapperImpl<T, R> implements Mapper<Payment, GetPaymentDTO> {

    /**
     * Converts a Payment entity to a GetPaymentDTO.
     *
     * <p>This method performs transformation of payment data including:</p>
     * <ul>
     *   <li>Currency enum to string conversion</li>
     *   <li>BigDecimal amount to string conversion for precise decimal representation</li>
     *   <li>Direct mapping of beneficiary and purpose information</li>
     * </ul>
     *
     * @param entity the Payment entity to convert; must not be null
     * @return a new GetPaymentDTO containing the mapped payment data
     * @throws IllegalArgumentException if the entity is null
     */
    @Contract(pure = true)
    @Override
    public @NotNull GetPaymentDTO toDto(final @NotNull Payment entity) {
        String currencyCode = entity.getCurrencyCode();
        if (currencyCode == null && entity.getAccount() != null && entity.getAccount().getCurrencyCode() != null) {
            currencyCode = entity.getAccount().getCurrencyCode().name();
        }

        String beneficiaryName = null;
        if (entity instanceof final IbanPayment ibanPayment) {
            beneficiaryName = ibanPayment.getRecipientName();
        } else if (entity instanceof final InternetPayment internetPayment) {
            beneficiaryName = internetPayment.getProviderName();
        } else if (entity instanceof final MobilePayment mobilePayment) {
            beneficiaryName = mobilePayment.getOperatorName();
        } else if (entity instanceof final UtilityPayment utilityPayment) {
            beneficiaryName = utilityPayment.getServiceProvider();
        }

        return new GetPaymentDTO(
                currencyCode,
                entity.getAmount() != null ? entity.getAmount().toString() : null,
                beneficiaryName,
                entity.getPurpose()
        );
    }
}
