package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("IBAN")
@Setter
@Getter
@NoArgsConstructor
public final class IbanPayment extends Payment {

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_iban")
    private String recipientIban;

    @Column(name = "tax_number")
    private String taxNumber;
}
