package ru.borshchevskiy.pcs.service.services.integration.email.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.common.enums.TeamMemberProjectRole;
import ru.borshchevskiy.pcs.dto.task.status.TaskDto;
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
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@RabbitListenerTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EmailServiceIT {

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
    private final AmqpAdmin amqpAdmin;

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres");

    private static final RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq")
                    .withExposedPorts(5672);


    static {
        postgresContainer.stop();
        rabbitMQContainer.stop();
        postgresContainer.start();
        rabbitMQContainer.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
    }


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
    void createTask() throws InterruptedException {
        final Long id = 1L;

        EmailService listener = harness.getSpy("newTask");
        assertNotNull(listener);

        TaskDto createRequest = new TaskDto();
        createRequest.setName("rabbitTask");
        createRequest.setProjectId(1L);
        createRequest.setLaborCosts(100);
        createRequest.setDeadline(LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0));
        createRequest.setDateCreated(LocalDateTime.now());
        createRequest.setStatus(TaskStatus.NEW);
        createRequest.setAuthorId(1L);
        createRequest.setImplementerId(1L);


        TaskDto actualResult = taskService.save(createRequest);
        Task savedTask = taskMapper.createTask(actualResult);
        savedTask.setId(1L);

        verify(listener, timeout(2000)).receiveNewTaskMessage(refEq(savedTask, "dateCreated", "dateUpdated"));
        verify(emailService, timeout(2000)).sendNewTaskNotification(refEq(savedTask, "dateCreated", "dateUpdated"));

    }
}