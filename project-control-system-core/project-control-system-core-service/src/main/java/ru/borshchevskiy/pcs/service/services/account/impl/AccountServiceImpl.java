package ru.borshchevskiy.pcs.service.services.account.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.UserAlreadyExistsException;
import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.service.mappers.account.AccountMapper;
import ru.borshchevskiy.pcs.service.services.account.AccountService;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepository repository;
    private final AccountMapper accountMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
    }

    @Override
    @Transactional
    public AccountDto save(AccountDto request) {
        return request.getId() == null
                ? create(request)
                : update(request);
    }

    public AccountDto create(AccountDto request) {
        Optional<Account> optionalAccount = repository.findByUsername(request.getUsername());

        if (optionalAccount.isPresent()) {
            throw new UserAlreadyExistsException("Username already exist!");
        }

        Account account = repository.save(accountMapper.createAccount(request));
        log.debug("Account " + account.getUsername() + " created.");
        return accountMapper.mapToDto(account);
    }

    public AccountDto update(AccountDto request) {

        Account account = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Account not found!"));

        account = accountMapper.mergeAccount(account, request);
        log.debug("Account id=" +  account.getId() + " updated.");
        return accountMapper.mapToDto(repository.saveAndFlush(account));
    }

    public AccountDto deleteById(AccountDto request) {

        Account account = repository.findById(request.getId())
                .map(a -> {
                    repository.delete(a);
                    return a;
                })
                .orElseThrow(() -> new NotFoundException("Account not found!"));
        log.debug("Account id=" +  account.getId() + " deleted.");
        return accountMapper.mapToDto(account);
    }

}
