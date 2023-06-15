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
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class EmployeeServiceDeleteIT extends IntegrationTestBase {


    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Employee employee1 = new Employee();
        employee1.setFirstname("Firstname1");
        employee1.setLastname("Lastname1");
        employee1.setStatus(EmployeeStatus.ACTIVE);

        employeeRepository.save(employee1);

        Employee employee2 = new Employee();
        employee2.setFirstname("Firstname2");
        employee2.setLastname("Lastname2");
        employee2.setStatus(EmployeeStatus.DELETED);

        employeeRepository.save(employee2);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
    }

    @Test
    void deleteSuccess() {
        final Long id = 1L;

        assertTrue(employeeRepository.findById(id).isPresent());

        EmployeeDto actualResult = employeeService.deleteById(id);

        assertNotNull(actualResult);
        assertThat(actualResult.getId()).isEqualTo(id);
        assertThat(actualResult.getStatus()).isEqualTo(EmployeeStatus.DELETED);
    }

    @Test
    void alreadyDeleted() {
        final Long id = 2L;

        assertTrue(employeeRepository.findById(id).isPresent());

        assertThrows(DeletedItemModificationException.class, () -> employeeService.deleteById(id));
    }

    @Test
    void employeeNotFound() {
        final Long id = Long.MIN_VALUE;

        assertThrows(NotFoundException.class, () -> employeeService.deleteById(id));
    }

}