package proeza.finapp.domain;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@DiscriminatorValue("E")
@Entity(name = "fin_Extraccion")
public class Withdrawal extends AccountMovement {
    public Withdrawal(Account account, BigDecimal monto) {
        super(account, monto, null);
    }
}