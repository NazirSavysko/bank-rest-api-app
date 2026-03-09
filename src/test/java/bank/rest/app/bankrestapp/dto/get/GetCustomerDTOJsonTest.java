package bank.rest.app.bankrestapp.dto.get;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetCustomerDTOJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeNestedAccountsWithAccountTypeAndEdrpou() throws Exception {
        final GetAccountDTO account = new GetAccountDTO(
                1,
                "UA000000000000000000000000000001",
                BigDecimal.TEN,
                "UAH",
                "ACTIVE",
                new GetCardDTO("1111", null, "123"),
                "FOP",
                "1234567890"
        );
        final GetCustomerDTO customer = new GetCustomerDTO(
                List.of(account),
                "John",
                "Doe",
                "john@example.com",
                "+380991112233"
        );

        final JsonNode json = this.objectMapper.readTree(this.objectMapper.writeValueAsString(customer));

        assertEquals("FOP", json.get("accounts").get(0).get("accountType").asText());
        assertEquals("1234567890", json.get("accounts").get(0).get("edrpou").asText());
    }
}
