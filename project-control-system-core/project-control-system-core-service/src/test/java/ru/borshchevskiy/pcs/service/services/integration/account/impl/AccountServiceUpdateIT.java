package ru.borshchevskiy.pcs.service.services.integration.account.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.Role;
import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.service.services.account.AccountService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class AccountServiceUpdateIT extends IntegrationTestBase {


    private final AccountService accountService;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("username");
        accountDto.setPassword("password");

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        accountDto.setRoles(roles);

        accountService.save(accountDto);
    }


    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.roles CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE roles_account_id_seq RESTART");
    }

    @Test
    void save() {

        final long expectedId = 1L;

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        AccountDto updateRequest = new AccountDto();
        updateRequest.setId(1L);
        updateRequest.setUsername("newUsername");
        updateRequest.setPassword("newPassword");
        updateRequest.setRoles(roles);

        AccountDto updatedAccount = accountService.save(updateRequest);

        assertThat(updatedAccount.getId()).isEqualTo(expectedId);
        assertThat(updatedAccount.getUsername()).isEqualTo("newUsername");
    }


}