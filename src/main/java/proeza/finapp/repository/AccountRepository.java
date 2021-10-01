package proeza.finapp.repository;

import org.springframework.data.repository.CrudRepository;
import proeza.finapp.domain.Account;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByNumber(String number);
}
