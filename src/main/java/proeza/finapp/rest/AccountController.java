package proeza.finapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proeza.finapp.domain.Account;
import proeza.finapp.domain.Deposit;
import proeza.finapp.domain.Withdrawal;
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

    @PutMapping("/{numero_cuenta}")
    public void update(@PathVariable("numero_cuenta") String number, @RequestBody Account account) {
        this.accountService.update(number, account);
    }

    @PostMapping("/{numero_cuenta}/extraccion")
    public Account withdraw(@PathVariable("numero_cuenta") String number, @RequestBody Withdrawal withdrawal) {
        return this.accountService.withdraw(withdrawal);
    }

    @PostMapping("/{numero_cuenta}/deposito")
    public Account deposit(@PathVariable("numero_cuenta") String number, @RequestBody Deposit deposit) {
        return this.accountService.deposit(deposit);
    }
}
