package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetCardDTO;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Mapper interface for converting Card entities to GetCardDTO objects.
 *
 * <p>This mapper handles the transformation of Card domain objects into Data Transfer Objects
 * suitable for API responses. It performs direct field mapping of card information.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Maps card number, expiry date, and CVV</li>
 *   <li>Performs direct field-to-field transformation</li>
 *   <li>Maintains security-sensitive information (CVV is exposed in this implementation)</li>
 * </ul>
 *
 * <p>Security Note: This implementation exposes the CVV in the DTO. In production environments,
 * consider masking or excluding sensitive card information from API responses.</p>
 *
 * <p>Example transformation:</p>
 * <pre>
 * Card entity with:
 * - cardNumber: "4111111111111111"
 * - expiryDate: "12/25"
 * - cvv: "123"
 *
 * Becomes GetCardDTO with:
 * - cardNumber: "4111111111111111"
 * - expiryDate: "12/25"
 * - cvv: "123"
 * </pre>
 *
 * @see bank.rest.app.bankrestapp.entity.Card
 * @see bank.rest.app.bankrestapp.dto.get.GetCardDTO
 * @see bank.rest.app.bankrestapp.mapper.Mapper
 *
 * @author Nazira Savisska
 * @since 1.0
 */
@Component
public final class CardMapperImpl implements Mapper<Card, GetCardDTO> {

    /**
     * Converts a Card entity to a GetCardDTO.
     *
     * <p>This method performs a straightforward mapping of all card fields.
     * Note that sensitive information like CVV is included in the mapping.</p>
     *
     * @param entity the Card entity to convert; must not be null
     * @return a new GetCardDTO containing the mapped card data
     * @throws IllegalArgumentException if the entity is null
     */
    @Contract("_ -> new")
    @Override
    public @NotNull GetCardDTO toDto(final @NotNull Card entity) {
        return new GetCardDTO(
                entity.getCardNumber(),
                entity.getExpiryDate(),
                entity.getCvv()
        );
    }
}
