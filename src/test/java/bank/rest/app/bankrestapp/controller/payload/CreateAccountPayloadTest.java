package bank.rest.app.bankrestapp.controller.payload;

import bank.rest.app.bankrestapp.entity.enums.AccountType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateAccountPayloadTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeAccountTypeAsEnumAndKeepCurrencyAsString() throws Exception {
        final String json = """
                {
                  "accountType": "FOP",
                  "currency": "USD"
                }
                """;

        final CreateAccountPayload payload = this.objectMapper.readValue(json, CreateAccountPayload.class);

        assertEquals(AccountType.FOP, payload.accountType());
        assertEquals("USD", payload.currency());
    }
}
