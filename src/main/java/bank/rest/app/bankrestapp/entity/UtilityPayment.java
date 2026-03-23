package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("UTILITY")
@Data
@EqualsAndHashCode(callSuper = true)
public final class UtilityPayment extends Payment {

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "utility_account_number")
    private String utilityAccountNumber;
}
