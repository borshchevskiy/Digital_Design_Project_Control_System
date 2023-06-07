package ru.borshchevskiy.pcs.service.services.integration.employee.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeServiceFindByUsernameIT extends IntegrationTestBase {


    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;


    @BeforeAll
    void prepare() {
        Account account1 = new Account();
        account1.setUsername("account1");
        account1.setPassword("password1");

        Account account2 = new Account();
        account2.setUsername("account2");
        account2.setPassword("password2");

        accountRepository.save(account1);
        accountRepository.save(account2);

        Employee employee1 = new Employee();
        employee1.setFirstname("testFirstName1");
        employee1.setLastname("testLastName1");
        employee1.setAccount(account1);
        employee1.setStatus(EmployeeStatus.ACTIVE);

        Employee employee2 = new Employee();
        employee2.setFirstname("testFirstName2");
        employee2.setLastname("testLastName2");
        employee2.setAccount(account2);
        employee2.setStatus(EmployeeStatus.ACTIVE);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
    }

    @AfterAll
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
    }

    @Test
    void employeeExists() {
        final String username = "account1";

        EmployeeDto actualResult = employeeService.findByUsername(username);

        assertNotNull(actualResult);
        assertThat(actualResult.getUsername()).isEqualTo(username);
    }

    @Test
    void employeeDoesntExist() {
        final String username = "wf3e4tgr";

        assertThrows(NotFoundException.class,
                () -> employeeService.findByUsername(username));
    }

}