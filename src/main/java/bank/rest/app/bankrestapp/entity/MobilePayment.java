package bank.rest.app.bankrestapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("MOBILE")
@Setter
@Getter
@NoArgsConstructor
public final class MobilePayment extends Payment {

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "phone_number")
    private String phoneNumber;
}
