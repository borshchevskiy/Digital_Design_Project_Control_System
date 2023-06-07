package ru.borshchevskiy.pcs.service.services.integration.employee.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeServiceSaveUpdateIT extends IntegrationTestBase {


    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeAll
    void prepare() {
        Account account = new Account();
        account.setUsername("username");
        account.setPassword("password");

        accountRepository.save(account);

        Employee employee1 = new Employee();
        employee1.setFirstname("Firstname");
        employee1.setLastname("Lastname");
        employee1.setAccount(account);
        employee1.setStatus(EmployeeStatus.ACTIVE);

        employeeRepository.save(employee1);

        Employee employee2 = new Employee();
        employee2.setFirstname("Firstname2");
        employee2.setLastname("Lastname2");
        employee2.setStatus(EmployeeStatus.DELETED);

        employeeRepository.save(employee2);
    }

    @AfterAll
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
    }

    @Test
    @Transactional
    @Rollback
    void updateEmployee() {

        final Long id = 1L;
        final String newFirstname = "newFirstname";
        final String newLastname = "newLastname";
        final String username = "username";

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

    @Test
    void updateEmployeeNotFound() {
        final Long id = Long.MIN_VALUE;

        final EmployeeDto requestDto = new EmployeeDto();
        requestDto.setId(id);

        assertThrows(NotFoundException.class, () -> employeeService.save(requestDto));
    }

    @Test
    void updateDeletedEmployee() {
        final Long id = 2L;

        assertTrue(employeeRepository.findById(id).isPresent());

        final EmployeeDto requestDto = new EmployeeDto();
        requestDto.setId(id);

        assertThrows(DeletedItemModificationException.class, () -> employeeService.save(requestDto));
    }


}