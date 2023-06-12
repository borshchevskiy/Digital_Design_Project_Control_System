package ru.borshchevskiy.pcs.service.services.integration.task.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class TaskServiceFindAllByFilterIT extends IntegrationTestBase {

    private final TaskService taskService;
    private final TaskRepository taskRepository;
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

        Task task1 = new Task();
        task1.setName("taskName1");
        task1.setProject(project);
        task1.setLaborCosts(100);
        task1.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        task1.setDateCreated(LocalDateTime.now());
        task1.setStatus(TaskStatus.NEW);
        task1.setAuthor(employee1);

        Task task2 = new Task();
        task2.setName("taskName2");
        task2.setProject(project);
        task2.setLaborCosts(100);
        task2.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        task2.setDateCreated(LocalDateTime.now());
        task2.setStatus(TaskStatus.NEW);
        task2.setAuthor(employee1);

        taskRepository.saveAll(List.of(task1, task2));

    }

    @AfterEach
    void cleanDatabase() {
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
    void findAllByFilterName() {

        TaskFilter filter = new TaskFilter("task", null, null, null, null, null);

        List<TaskDto> all = taskService.findAllByFilter(filter);

        assertNotNull(all);
        assertThat(all.size()).isEqualTo(2L);
    }

    @Test
    void findAllByFilterNameAndStatus() {

        TaskFilter filter = new TaskFilter("t", TaskStatus.NEW, null, null, null, null);

        List<TaskDto> all = taskService.findAllByFilter(filter);

        assertNotNull(all);
        assertThat(all.size()).isEqualTo(2L);
    }

    @Test
    void findOne() {

        TaskFilter filter = new TaskFilter("1", null, null, null, null, null);

        List<TaskDto> all = taskService.findAllByFilter(filter);

        assertNotNull(all);
        assertThat(all.size()).isEqualTo(1L);
    }

    @Test
    void findNone() {

        TaskFilter filter = new TaskFilter("3", null, null, null, null, null);

        List<TaskDto> all = taskService.findAllByFilter(filter);

        assertNotNull(all);
        assertThat(all.size()).isEqualTo(0L);
    }


}
