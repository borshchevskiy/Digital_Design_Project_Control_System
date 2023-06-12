package ru.borshchevskiy.pcs.service.services.integration.task.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TaskServiceSaveIT extends IntegrationTestBase {


    private final TaskService taskService;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepareTestData() {
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

        Project project = new Project();
        project.setCode("project1");
        project.setName("Project 1");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.tasks CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE tasks_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
    }

    @Test
    void taskNameIsEmpty() {

        TaskDto createRequest = new TaskDto();

        createRequest.setProjectId(1L);
        createRequest.setLaborCosts(100);
        createRequest.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        createRequest.setDateCreated(LocalDateTime.now());
        createRequest.setStatus(TaskStatus.NEW);
        createRequest.setAuthorId(1L);

        assertThrows(RequestDataValidationException.class, () -> taskService.save(createRequest));
    }

    @Test
    void laborCostsIsEmpty() {

        TaskDto createRequest = new TaskDto();
        createRequest.setName("taskName");
        createRequest.setProjectId(1L);
        createRequest.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        createRequest.setDateCreated(LocalDateTime.now());
        createRequest.setStatus(TaskStatus.NEW);
        createRequest.setAuthorId(1L);

        assertThrows(RequestDataValidationException.class, () -> taskService.save(createRequest));
    }

    @Test
    void deadlineIsEmpty() {

        TaskDto createRequest = new TaskDto();
        createRequest.setName("taskName");
        createRequest.setProjectId(1L);
        createRequest.setLaborCosts(100);
        createRequest.setDateCreated(LocalDateTime.now());
        createRequest.setStatus(TaskStatus.NEW);
        createRequest.setAuthorId(1L);

        assertThrows(RequestDataValidationException.class, () -> taskService.save(createRequest));
    }

    @Test
    void projectNotExists() {
        TaskDto createRequest = new TaskDto();
        createRequest.setName("taskName");
        createRequest.setLaborCosts(100);
        createRequest.setProjectId(Long.MIN_VALUE);
        createRequest.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        createRequest.setDateCreated(LocalDateTime.now());
        createRequest.setStatus(TaskStatus.NEW);
        createRequest.setAuthorId(1L);

        assertThrows(NotFoundException.class, () -> taskService.save(createRequest));
    }

    @Test
    @WithMockUser(value = "username1", password = "password1")
    void authorIsNotInTeam() {

        TaskDto createRequest = new TaskDto();
        createRequest.setName("taskName");
        createRequest.setLaborCosts(100);
        createRequest.setProjectId(1L);
        createRequest.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        createRequest.setDateCreated(LocalDateTime.now());
        createRequest.setStatus(TaskStatus.NEW);
        createRequest.setAuthorId(1L);

        assertThrows(RequestDataValidationException.class, () -> taskService.save(createRequest));
    }

    @Test
    @WithMockUser(value = "username1", password = "password1")
    void implementerIsNotInTeam() {

        TaskDto createRequest = new TaskDto();
        createRequest.setName("taskName");
        createRequest.setLaborCosts(100);
        createRequest.setProjectId(1L);
        createRequest.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        createRequest.setDateCreated(LocalDateTime.now());
        createRequest.setStatus(TaskStatus.NEW);
        createRequest.setAuthorId(1L);
        createRequest.setImplementerId(1L);

        assertThrows(RequestDataValidationException.class, () -> taskService.save(createRequest));
    }
}