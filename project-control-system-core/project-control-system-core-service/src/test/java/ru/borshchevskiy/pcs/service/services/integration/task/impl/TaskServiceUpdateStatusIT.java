package ru.borshchevskiy.pcs.service.services.integration.task.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.StatusModificationException;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;
import ru.borshchevskiy.pcs.repository.teammember.TeamMemberRepository;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TaskServiceUpdateStatusIT extends IntegrationTestBase {


    private final TaskService taskService;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    @Transactional
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

        Project project = new Project();
        project.setCode("project1");
        project.setName("Project 1");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);

        Task task = new Task();
        task.setName("taskName");
        task.setProject(project);
        task.setLaborCosts(100);
        task.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        task.setDateCreated(LocalDateTime.now());
        task.setStatus(TaskStatus.NEW);
        task.setAuthor(employee1);

        taskRepository.save(task);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.tasks CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.teams CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.team_members CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE tasks_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE teams_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE team_members_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
    }

    @Test
    void updateStatus() {

        final Long id = 1L;

        assertTrue(taskRepository.findById(id).isPresent());

        TaskStatusDto statusDto = new TaskStatusDto();
        statusDto.setStatus(TaskStatus.IN_WORK);

        TaskDto actualResult = taskService.updateStatus(id, statusDto);

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getStatus()).isEqualTo(TaskStatus.IN_WORK);
    }

    @Test
    void updateStatusIncorrect() {

        final Long id = 1L;

        assertTrue(taskRepository.findById(id).isPresent());

        TaskStatusDto statusDto = new TaskStatusDto();
        statusDto.setStatus(TaskStatus.CLOSED);

        assertThrows(StatusModificationException.class, () -> taskService.updateStatus(id, statusDto));
    }

    @Test
    void taskNotFound() {

        final Long id = Long.MIN_VALUE;

        TaskStatusDto statusDto = new TaskStatusDto();
        statusDto.setStatus(TaskStatus.CLOSED);

        assertThrows(NotFoundException.class, () -> taskService.updateStatus(id, statusDto));
    }

}