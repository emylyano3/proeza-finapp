package proeza.finapp.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import proeza.finapp.entities.Account;
import proeza.finapp.entities.Deposit;
import proeza.finapp.entities.Withdrawal;
import proeza.finapp.repository.AccountRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    public Account findByNumber(String number) {
        return this.accountRepo.findByNumber(number)
                          .orElseThrow(accountNotFound(number));
    }

    public Account create(Account account) {
        boolean exists = Optional.ofNullable(account.getId())
                                 .map(id -> this.accountRepo.findById(id).isPresent())
                                 .orElse(false);
        if (exists) {
            return account;
        } else {
            return this.accountRepo.save(account);
        }
    }

    @Transactional
    public void update(String number, Account account) {
        if (!Objects.equals(number, account.getNumber())) {
            throw new IllegalArgumentException("El numero de la cuenta a actualizar no coincide con el de la cuenta recibida");
        }
        Optional.ofNullable(number)
                .map(this::findByNumber)
                .ifPresent(b -> this.accountRepo.save(account));
    }

    public Account deposit(Deposit deposit) {
        Objects.requireNonNull(deposit);
        Objects.requireNonNull(deposit.getAccount());
        Objects.requireNonNull(deposit.getAccount().getNumber());
        Objects.requireNonNull(deposit.getMonto());
        Account account = findByNumber(deposit.getAccount().getNumber());
        deposit.setFecha(deposit.getFecha() == null
                ? LocalDateTime.now()
                : deposit.getFecha());
        deposit.setAccount(account);
        account.addDeposito(deposit);
        return account;
    }

    public Account withdraw(Withdrawal withdrawal) {
        Objects.requireNonNull(withdrawal);
        Objects.requireNonNull(withdrawal.getAccount());
        Objects.requireNonNull(withdrawal.getAccount().getNumber());
        Objects.requireNonNull(withdrawal.getMonto());
        Account account = findByNumber(withdrawal.getAccount().getNumber());
        withdrawal.setFecha(withdrawal.getFecha() == null
                ? LocalDateTime.now()
                : withdrawal.getFecha());
        withdrawal.setAccount(account);
        account.addExtraccion(withdrawal);
        return account;
    }

    @NotNull
    private Supplier<ResourceNotFoundException> accountNotFound(String number) {
        return () -> new ResourceNotFoundException(String.format("Cuenta con numero %s no encontrada", number));
    }
}
