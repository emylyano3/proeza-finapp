package proeza.finapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"movements"})
@Entity(name = "fin_Cuenta")
@Table(name = "fin_cuenta")
public class Account extends IdEntity<Long> {

    @Column(name="saldo", nullable = false, scale = 3, precision = 10)
    private BigDecimal balance;

    @Column(name="numero", nullable = false, length = 20)
    private String number;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<AccountMovement> movements = new ArrayList<>(0);

    /**
     * Agrega el deposito a los movimientos asociados a la cuenta y actualiza el saldo.
     *
     * @param deposit El deposito a impactar en la cuenta
     */
    @Transient
    public void apply(Deposit deposit) {
        movements.add(deposit);
        credit(deposit.getAmount());
    }

    /**
     * Agrega la extraccion a los movimientos asociados a la cuenta y actualiza el saldo.
     *
     * @param withdrawal La extraccion a impactar en la cuenta
     */
    @Transient
    public void apply(Withdrawal withdrawal) {
        movements.add(withdrawal);
        debit(withdrawal.getAmount());
    }

    @Transient
    private void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    @Transient
    private void debit(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}
