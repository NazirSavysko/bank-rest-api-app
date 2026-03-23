package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("TRAVEL")
@Data
@EqualsAndHashCode(callSuper = true)
public class TravelPayment extends Payment {
}
