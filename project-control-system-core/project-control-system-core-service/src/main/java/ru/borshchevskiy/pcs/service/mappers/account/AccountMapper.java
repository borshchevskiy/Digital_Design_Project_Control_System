package ru.borshchevskiy.pcs.service.mappers.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.common.enums.Role;
import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountDto mapToDto(Account account) {
        AccountDto accountDto = new AccountDto();

        accountDto.setId(account.getId());
        accountDto.setUsername(account.getUsername());
        accountDto.setPassword(null);
        accountDto.setRoles(account.getRoles());

        return accountDto;
    }

    public EmployeeDto mapToEmployeeDto(AccountDto accountDto) {
        EmployeeDto employeeDto = new EmployeeDto();

        employeeDto.setUsername(accountDto.getUsername());
        employeeDto.setFirstname(accountDto.getFirstname());
        employeeDto.setLastname(accountDto.getLastname());

        return employeeDto;
    }

    public Account createAccount(AccountDto dto) {
        Account account = new Account();

        copyToAccount(account, dto);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        account.setRoles(roles);

        return account;
    }

    public Account mergeAccount(Account account, AccountDto dto) {

        return copyToAccount(account, dto);
    }

    private Account getAccount(String username) {
        return Optional.ofNullable(username)
                .flatMap(accountRepository::findByUsername)
                .orElse(null);
    }

    private Account copyToAccount(Account copyTo, AccountDto copyFrom) {

        copyTo.setUsername(copyFrom.getUsername());
        copyTo.setPassword(passwordEncoder.encode(copyFrom.getPassword()));
        copyTo.setRoles(copyFrom.getRoles());

        return copyTo;
    }
}
