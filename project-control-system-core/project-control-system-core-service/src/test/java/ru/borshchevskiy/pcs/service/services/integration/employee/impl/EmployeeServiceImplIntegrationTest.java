package ru.borshchevskiy.pcs.service.services.integration.employee.impl;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class EmployeeServiceImplIntegrationTest extends IntegrationTestBase {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FindById {

        @BeforeAll
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

        @AfterAll
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindAll {

        @BeforeAll
        public void prepare() {
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
        public void clean() {
            jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
            jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        }

        @Test
        @Order(10)
        void employeesExist() {

            List<EmployeeDto> all = employeeService.findAll();

            assertNotNull(all);
            assertThat(all.size()).isEqualTo(2L);
        }


        @Test
        @Order(20)
        void findEmptyList() {

            List<EmployeeDto> all = employeeService.findAll();

            assertNotNull(all);
            assertThat(all).isEmpty();
        }


    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FindAllByFilter {

        @BeforeAll
        void prepare() {
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

        @AfterAll
        void clean() {
            jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
            jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        }

        @Test
        void findAll() {

            EmployeeFilter employeeFilter = new EmployeeFilter("test");

            List<EmployeeDto> all = employeeService.findAllByFilter(employeeFilter);

            assertNotNull(all);
            assertThat(all.size()).isEqualTo(2L);
        }

        @Test
        void findOne() {

            EmployeeFilter employeeFilter = new EmployeeFilter("1");

            List<EmployeeDto> all = employeeService.findAllByFilter(employeeFilter);

            assertNotNull(all);
            assertThat(all.size()).isEqualTo(1);
        }

        @Test
        void findNone() {

            EmployeeFilter employeeFilter = new EmployeeFilter("f^*%&@^e^");

            List<EmployeeDto> all = employeeService.findAllByFilter(employeeFilter);

            assertNotNull(all);
            assertThat(all).isEmpty();
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FindByUsername {

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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SaveCreate {
        @BeforeAll
        void prepare() {
            Account account1 = new Account();
            account1.setUsername("account1");
            account1.setPassword("password1");

            accountRepository.save(account1);
        }

        @AfterAll
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SaveUpdate {
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Delete {
        @BeforeAll
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

        @AfterAll
        void clean() {
            jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
            jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
            jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
        }

        @Test
        @Transactional
        @Rollback
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
}