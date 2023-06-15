package ru.borshchevskiy.pcs.service.services.integration.account.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.Role;
import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.service.services.account.AccountService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class AccountServiceSaveIT extends IntegrationTestBase {


    private final AccountService accountService;
    private final JdbcTemplate jdbcTemplate;


    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.roles CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE roles_account_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
    }

    @Test
    void save() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("username");
        accountDto.setPassword("password");
        accountDto.setFirstname("Firstname");
        accountDto.setLastname("Lastname");
        accountDto.setRoles(roles);

        final long expectedId = 1L;

        EmployeeDto actualResult = accountService.save(accountDto);

        assertThat(actualResult.getId()).isEqualTo(expectedId);
        assertThat(actualResult.getUsername()).isEqualTo(accountDto.getUsername());
        assertThat(actualResult.getFirstname()).isEqualTo(accountDto.getFirstname());
        assertThat(actualResult.getLastname()).isEqualTo(accountDto.getLastname());
    }


}