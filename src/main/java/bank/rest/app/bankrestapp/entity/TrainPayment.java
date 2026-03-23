package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("TRAIN")
@Data
@EqualsAndHashCode(callSuper = true)
public class TrainPayment extends Payment {
}
