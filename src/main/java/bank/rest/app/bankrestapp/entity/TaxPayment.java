package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TAX")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaxPayment extends Payment {
}
