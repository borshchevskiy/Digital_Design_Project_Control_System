package ru.borshchevskiy.pcs.service.services.integration.employee.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class EmployeeServiceSaveCreateIT extends IntegrationTestBase {


    private final EmployeeService employeeService;
    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Account account1 = new Account();
        account1.setUsername("account1");
        account1.setPassword("password1");

        accountRepository.save(account1);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
    }

    @Test
    void createEmployee() {

        EmployeeDto createRequest = new EmployeeDto();
        createRequest.setFirstname("Firstname");
        createRequest.setLastname("Lastname");
        createRequest.setUsername("account1");
        createRequest.setStatus(EmployeeStatus.ACTIVE);


        EmployeeDto actualResult = employeeService.save(createRequest);


        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getUsername()).isEqualTo("account1");
        assertThat(actualResult.getFirstname()).isEqualTo("Firstname");

    }

}