package ru.borshchevskiy.pcs.service.services.integration.employee.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class EmployeeServiceFindByIdIT extends IntegrationTestBase {


    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final JdbcTemplate jdbcTemplate;


    @BeforeEach
    void prepareTestData() {
        Employee employee1 = new Employee();
        employee1.setFirstname("testFirstName1");
        employee1.setLastname("testLastName1");
        employee1.setStatus(EmployeeStatus.ACTIVE);

        Employee employee2 = new Employee();
        employee2.setFirstname("testFirstName2");
        employee2.setLastname("testLastName2");
        employee2.setStatus(EmployeeStatus.ACTIVE);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
    }

    @Test
    void employeeExists() {
        final long expectedId = 1L;

        EmployeeDto employeeById = employeeService.findById(expectedId);

        assertThat(employeeById.getId()).isEqualTo(expectedId);
    }

    @Test
    void employeeDoesntExist() {
        final Long id = Long.MIN_VALUE;

        assertThrows(NotFoundException.class, () -> employeeService.findById(id));
    }


}