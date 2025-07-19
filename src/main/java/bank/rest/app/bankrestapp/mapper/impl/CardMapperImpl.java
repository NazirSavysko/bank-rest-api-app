package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.get.GetCardDTO;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public final class CardMapperImpl implements Mapper<Card, GetCardDTO> {

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
