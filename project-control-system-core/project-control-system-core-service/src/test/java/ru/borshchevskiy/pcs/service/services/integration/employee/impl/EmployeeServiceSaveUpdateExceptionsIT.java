package ru.borshchevskiy.pcs.service.services.integration.employee.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class EmployeeServiceSaveUpdateExceptionsIT extends IntegrationTestBase {


    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Account account1 = new Account();
        account1.setUsername("username1");
        account1.setPassword("password1");

        Account account2 = new Account();
        account2.setUsername("username2");
        account2.setPassword("password2");

        accountRepository.save(account1);
        accountRepository.save(account2);

        Employee employee1 = new Employee();
        employee1.setFirstname("Firstname1");
        employee1.setLastname("Lastname1");
        employee1.setAccount(account1);
        employee1.setStatus(EmployeeStatus.ACTIVE);

        Employee employee2 = new Employee();
        employee2.setFirstname("Firstname2");
        employee2.setLastname("Lastname2");
        employee2.setAccount(account2);
        employee2.setStatus(EmployeeStatus.ACTIVE);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        Employee deletedEmployee = new Employee();
        deletedEmployee.setFirstname("Firstname");
        deletedEmployee.setLastname("Lastname");
        deletedEmployee.setStatus(EmployeeStatus.DELETED);

        employeeRepository.save(deletedEmployee);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
    }


    @Test
    void updateEmployeeNotFound() {
        final Long id = Long.MIN_VALUE;

        final EmployeeDto requestDto = new EmployeeDto();
        requestDto.setId(id);
        requestDto.setFirstname("Firstname");
        requestDto.setLastname("Lastname");

        assertThrows(NotFoundException.class, () -> employeeService.save(requestDto));
    }

    @Test
    void updateDeletedEmployee() {
        final Long id = 3L;

        assertTrue(employeeRepository.findById(id).isPresent());

        final EmployeeDto requestDto = new EmployeeDto();
        requestDto.setId(id);
        requestDto.setFirstname("Firstname");
        requestDto.setLastname("Lastname");

        assertThrows(DeletedItemModificationException.class, () -> employeeService.save(requestDto));
    }

    @Test
    void changeStatus() {
        final Long id = 1L;

        assertTrue(employeeRepository.findById(id).isPresent());

        final String newFirstname = "newFirstname";
        final String newLastname = "newLastname";
        final String username = "username1";


        EmployeeDto updateRequest = new EmployeeDto();
        updateRequest.setId(id);
        updateRequest.setFirstname(newFirstname);
        updateRequest.setLastname(newLastname);
        updateRequest.setUsername(username);
        updateRequest.setStatus(null);

        assertThrows(RequestDataValidationException.class, () -> employeeService.save(updateRequest));
    }

    @Test
    void changeAccountToAnotherHavingEmployee() {
        final Long id = 1L;

        assertTrue(employeeRepository.findById(id).isPresent());

        final String newFirstname = "newFirstname";
        final String newLastname = "newLastname";
        final String username = "username2";


        EmployeeDto updateRequest = new EmployeeDto();
        updateRequest.setId(id);
        updateRequest.setFirstname(newFirstname);
        updateRequest.setLastname(newLastname);
        updateRequest.setUsername(username);
        updateRequest.setStatus(EmployeeStatus.ACTIVE);

        assertThrows(RequestDataValidationException.class, () -> employeeService.save(updateRequest));
    }

}