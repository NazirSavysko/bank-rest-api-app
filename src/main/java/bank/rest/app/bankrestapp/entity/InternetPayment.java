package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("INTERNET")
@Setter
@Getter
@NoArgsConstructor
public final class InternetPayment extends Payment {

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "contract_number")
    private String contractNumber;
}
