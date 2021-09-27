package proeza.finapp.domain;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@NoArgsConstructor
@DiscriminatorValue("D")
@Entity(name = "fin_Deposito")
public class Deposit extends AccountMovement {
    public Deposit(Account account, BigDecimal monto) {
        super(account, monto, null);
    }
}