package ru.borshchevskiy.pcs.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borshchevskiy.pcs.entities.account.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}
