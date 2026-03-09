package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("UTILITY")
@Setter
@Getter
@NoArgsConstructor
public final class UtilityPayment extends Payment {

    @Column(name = "service_provider")
    private String serviceProvider;

    @Column(name = "client_address")
    private String clientAddress;

    @Column(name = "utility_account_number")
    private String utilityAccountNumber;
}
