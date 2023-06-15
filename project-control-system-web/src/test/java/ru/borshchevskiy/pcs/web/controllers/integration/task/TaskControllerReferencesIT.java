package ru.borshchevskiy.pcs.web.controllers.integration.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.borshchevskiy.pcs.common.enums.*;
import ru.borshchevskiy.pcs.dto.task.reference.TaskReferenceDto;
import ru.borshchevskiy.pcs.dto.task.status.TaskDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.repository.teammember.TeamMemberRepository;
import ru.borshchevskiy.pcs.service.mappers.task.TaskMapper;
import ru.borshchevskiy.pcs.service.services.task.TaskService;
import ru.borshchevskiy.pcs.web.controllers.integration.IntegrationTestBase;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TaskControllerReferencesIT extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskService taskService;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private Account account;
    private TaskDto dto1;
    private TaskDto dto2;
    private List<TaskDto> tasks = new ArrayList<>();

    @BeforeEach
    void prepare() {

        account = new Account();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        account.setUsername("username");
        account.setPassword("password");
        account.setRoles(roles);

        Account implementerAccount = new Account();
        roles.add(Role.USER);
        implementerAccount.setUsername("implementer");
        implementerAccount.setPassword("password");
        implementerAccount.setRoles(roles);

        accountRepository.save(account);
        accountRepository.save(implementerAccount);

        Employee author = new Employee();
        author.setFirstname("author");
        author.setLastname("author");
        author.setAccount(account);
        author.setStatus(EmployeeStatus.ACTIVE);

        Employee implementer = new Employee();
        implementer.setFirstname("implementer");
        implementer.setLastname("implementer");
        implementer.setAccount(implementerAccount);
        implementer.setStatus(EmployeeStatus.ACTIVE);

        employeeRepository.save(author);
        employeeRepository.save(implementer);

        Project project1 = new Project();
        project1.setCode("project1");
        project1.setName("Project 1");
        project1.setStatus(ProjectStatus.DRAFT);

        Project project2 = new Project();
        project2.setCode("project2");
        project2.setName("Project 2");
        project2.setStatus(ProjectStatus.DRAFT);

        Project savedProject1 = projectRepository.save(project1);
        Project savedProject2 = projectRepository.save(project2);

        Team team1 = new Team();
        team1.setProject(savedProject1);

        Team team2 = new Team();
        team2.setProject(savedProject2);

        teamRepository.save(team1);
        teamRepository.save(team2);

        TeamMember member1 = new TeamMember();
        member1.setTeam(team1);
        member1.setEmployee(author);
        member1.setRole(TeamMemberProjectRole.PROJECT_MANAGER);

        TeamMember member2 = new TeamMember();
        member2.setTeam(team1);
        member2.setEmployee(implementer);
        member2.setRole(TeamMemberProjectRole.DEVELOPER);

        teamMemberRepository.save(member1);
        teamMemberRepository.save(member2);

        TaskDto request = new TaskDto();
        request.setName("Task 1");
        request.setImplementerId(1L);
        request.setLaborCosts(100);
        request.setDeadline(LocalDateTime.of(2023, 12, 31, 0, 0));
        request.setStatus(TaskStatus.NEW);
        request.setAuthorId(2L);
        request.setProjectId(1L);

        TaskDto request2 = new TaskDto();
        request2.setName("Task 2");
        request2.setImplementerId(1L);
        request2.setLaborCosts(200);
        request2.setDeadline(LocalDateTime.of(2023, 12, 31, 0, 0));
        request2.setStatus(TaskStatus.NEW);
        request2.setAuthorId(2L);
        request2.setProjectId(1L);

        TaskDto request3 = new TaskDto();
        request3.setName("Task 2");
        request3.setImplementerId(1L);
        request3.setLaborCosts(200);
        request3.setDeadline(LocalDateTime.of(2023, 12, 31, 0, 0));
        request3.setStatus(TaskStatus.NEW);
        request3.setAuthorId(2L);
        request3.setProjectId(1L);

        taskService.save(request);
        taskService.save(request2);
        taskService.save(request3);

    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.roles CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.teams CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.team_members CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.tasks CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE roles_account_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE teams_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE team_members_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE tasks_id_seq RESTART");
    }


    @Test

    @WithMockUser(username = "username", password = "password")
    public void addReference() throws Exception {

        TaskReferenceDto request = new TaskReferenceDto(List.of(2L));


        mockMvc.perform(post("/api/v1/tasks/1/references/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(account)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(taskService.findById(2L)))));
    }

    @Test

    @WithMockUser(username = "username", password = "password")
    public void removeReference() throws Exception {

        TaskReferenceDto request = new TaskReferenceDto(List.of(2L));

        // Добавляем связи и убеждаемся что они применились
        mockMvc.perform(post("/api/v1/tasks/1/references/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(account)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(taskService.findById(2L)))));

        // Удаляем связь
        mockMvc.perform(post("/api/v1/tasks/1/references/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(account)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(Collections.EMPTY_LIST)));
    }

    @Test

    @WithMockUser(username = "username", password = "password")
    public void getReferences() throws Exception {

        TaskReferenceDto request = new TaskReferenceDto(List.of(2L, 3L));

        // Добавляем связи и убеждаемся что они применились
        mockMvc.perform(post("/api/v1/tasks/1/references/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(account)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(taskService.findById(2L),
                                taskService.findById(3L)))));

        // Получаем список связей
        mockMvc.perform(get("/api/v1/tasks/1/references")
                        .with(user(account)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(taskService.findById(2L),
                                taskService.findById(3L)))));
    }
}