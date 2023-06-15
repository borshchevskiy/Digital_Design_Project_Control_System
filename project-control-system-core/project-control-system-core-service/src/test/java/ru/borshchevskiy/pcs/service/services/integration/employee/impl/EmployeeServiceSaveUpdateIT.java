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
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class EmployeeServiceSaveUpdateIT extends IntegrationTestBase {


    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Account account1 = new Account();
        account1.setUsername("username1");
        account1.setPassword("password1");

        accountRepository.save(account1);

        Employee employee1 = new Employee();
        employee1.setFirstname("Firstname1");
        employee1.setLastname("Lastname1");
        employee1.setAccount(account1);
        employee1.setStatus(EmployeeStatus.ACTIVE);

        employeeRepository.save(employee1);

    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
    }

    @Test
    void updateEmployee() {

        final Long id = 1L;
        final String newFirstname = "newFirstname";
        final String newLastname = "newLastname";
        final String username = "username1";

        assertTrue(employeeRepository.findById(id).isPresent());

        EmployeeDto updateRequest = new EmployeeDto();
        updateRequest.setId(id);
        updateRequest.setFirstname(newFirstname);
        updateRequest.setLastname(newLastname);
        updateRequest.setUsername(username);
        updateRequest.setStatus(EmployeeStatus.ACTIVE);

        EmployeeDto actualResult = employeeService.save(updateRequest);

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getUsername()).isEqualTo(username);
        assertThat(actualResult.getFirstname()).isEqualTo(newFirstname);
        assertThat(actualResult.getLastname()).isEqualTo(newLastname);
    }

}