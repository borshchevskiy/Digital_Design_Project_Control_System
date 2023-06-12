package ru.borshchevskiy.pcs.service.services.integration.email.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.common.enums.TeamMemberProjectRole;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.repository.teammember.TeamMemberRepository;
import ru.borshchevskiy.pcs.service.mappers.task.TaskMapper;
import ru.borshchevskiy.pcs.service.services.email.EmailService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@RabbitListenerTest
class EmailServiceIT extends IntegrationTestBase {


    private final TaskService taskService;
    @SpyBean
    private final EmailService emailService;
    private final TaskMapper taskMapper;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;
    private final RabbitListenerTestHarness harness;

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
        employee1.setEmail("implementer@gmail.com");

        employeeRepository.save(employee1);

        Project project = new Project();
        project.setCode("project1");
        project.setName("Project 1");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);

        Team team = new Team();
        team.setProject(project);

        teamRepository.save(team);

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setRole(TeamMemberProjectRole.DEVELOPER);
        member.setEmployee(employee1);

        teamMemberRepository.save(member);
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
    @WithMockUser(value = "username1", password = "password1")
    @Transactional
    @Rollback
    void createTask() {
        final Long id = 1L;

        EmailService listener = harness.getSpy("newTask");
        assertNotNull(listener);

        TaskDto createRequest = new TaskDto();
        createRequest.setName("taskName");

        createRequest.setProjectId(1L);
        createRequest.setLaborCosts(100);
        createRequest.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        createRequest.setDateCreated(LocalDateTime.now());
        createRequest.setStatus(TaskStatus.NEW);
        createRequest.setAuthorId(1L);
        createRequest.setImplementerId(1L);

        TaskDto actualResult = taskService.save(createRequest);

        Task savedTask = taskMapper.createTask(actualResult, actualResult.getDateCreated());
        savedTask.setId(1L);

        verify(listener).receiveNewTaskMessage(savedTask);
        verify(emailService).sendNewTaskNotification(savedTask);

    }
}