package ru.borshchevskiy.pcs.service.services.account.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.exceptions.AuthException;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.common.exceptions.UserAlreadyExistsException;
import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.mappers.account.AccountMapper;
import ru.borshchevskiy.pcs.service.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.service.services.account.AccountService;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepository repository;
    private final EmployeeRepository employeeRepository;
    private final AccountMapper accountMapper;
    private final EmployeeService employeeService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Account account = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Employee profile not found for this username"));

        if (employee.getStatus() == EmployeeStatus.DELETED) {
            throw new AuthException("Profile for this username is deleted.");
        }

        return account;
    }

    @Override
    @Transactional
    public EmployeeDto save(AccountDto request) {

        if (!StringUtils.hasText(request.getUsername())
            || !StringUtils.hasText(request.getPassword())
            || !StringUtils.hasText(request.getFirstname())
            || !StringUtils.hasText(request.getLastname())) {
            throw new RequestDataValidationException("Username, Password, Firstname and Lastname must be specified.");
        }

        return request.getId() == null
                ? create(request)
                : update(request);
    }

    private EmployeeDto create(AccountDto request) {

        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exist!");
        }

        Account savedAccount = repository.save(accountMapper.createAccount(request));
        log.debug("Account " + savedAccount.getUsername() + " created.");

        // Автоматически создаем профиль сотрудника для учетной записи и возвращаем
        return employeeService.save(accountMapper.mapToEmployeeDto(request));
    }

    private EmployeeDto update(AccountDto request) {

        Account account = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Account not found!"));

        repository.saveAndFlush(accountMapper.mergeAccount(account, request));
        log.debug("Account id=" + account.getId() + " updated.");


        EmployeeDto employeeDto = employeeService.findByUsername(request.getUsername());

        if (!request.getFirstname().equals(employeeDto.getFirstname())
            || !request.getLastname().equals(employeeDto.getLastname())) {
            employeeDto.setFirstname(request.getFirstname());
            employeeDto.setLastname(request.getLastname());
        }


        EmployeeDto updatedEmployee = employeeService.save(employeeDto);

        return employeeDto;
    }

    @Override
    @Transactional
    public AccountDto deleteById(AccountDto request) {

        Account account = repository.findById(request.getId())
                .map(a -> {
                    repository.delete(a);
                    return a;
                })
                .orElseThrow(() -> new NotFoundException("Account not found!"));
        log.debug("Account id=" + account.getId() + " deleted.");
        return accountMapper.mapToDto(account);
    }

}
