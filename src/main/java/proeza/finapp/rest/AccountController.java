package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proeza.finapp.entities.Account;
import proeza.finapp.entities.Deposit;
import proeza.finapp.entities.Withdrawal;
import proeza.finapp.service.AccountService;

@CrossOrigin
@RestController
@RequestMapping("api/cuenta")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{numero_cuenta}")
    public Account get(@PathVariable("numero_cuenta") String number) {
        return this.accountService.findByNumber(number);
    }

    @PostMapping
    public Account create(@RequestBody Account account) {
        return this.accountService.create(account);
    }

    @PutMapping
    public void update(@PathVariable("numero_cuenta") String number, @RequestBody Account account) {
        this.accountService.update(number, account);
    }

    @PostMapping("/{numero_cuenta}/extraccion")
    public Account withdraw(@RequestBody Withdrawal withdrawal) {
        return this.accountService.withdraw(withdrawal);
    }

    @PostMapping("/{numero_cuenta}/deposito")
    public Account deposit(@RequestBody Deposit deposit) {
        return this.accountService.deposit(deposit);
    }
}
