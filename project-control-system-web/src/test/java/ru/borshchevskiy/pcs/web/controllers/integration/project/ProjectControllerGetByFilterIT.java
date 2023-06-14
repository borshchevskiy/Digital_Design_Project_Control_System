package ru.borshchevskiy.pcs.web.controllers.integration.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.enums.Role;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.dto.project.filter.ProjectFilter;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.mappers.project.ProjectMapper;
import ru.borshchevskiy.pcs.web.controllers.integration.IntegrationTestBase;

import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ProjectControllerGetByFilterIT extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private Account account;
    private ProjectDto dto1;
    private ProjectDto dto2;
    private ProjectDto dto3;
    private final List<ProjectDto> projects = new ArrayList<>();

    @BeforeEach
    void prepare() {

        account = new Account();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        account.setUsername("username");
        account.setPassword("password");
        account.setRoles(roles);
        accountRepository.save(account);

        Project project1 = new Project();
        project1.setCode("project1");
        project1.setName("Project 1");
        project1.setStatus(ProjectStatus.DRAFT);

        Project project2 = new Project();
        project2.setCode("project2");
        project2.setName("Project 2");
        project2.setStatus(ProjectStatus.DRAFT);

        Project project3 = new Project();
        project3.setCode("project3");
        project3.setName("Project 3");
        project3.setStatus(ProjectStatus.DRAFT);

        Project savedProject1 = projectRepository.save(project1);
        Project savedProject2 = projectRepository.save(project2);
        Project savedProject3 = projectRepository.save(project3);

        dto1 = projectMapper.mapToDto(savedProject1);
        dto2 = projectMapper.mapToDto(savedProject2);
        dto3 = projectMapper.mapToDto(savedProject3);

        projects.add(dto1);
        projects.add(dto2);
        projects.add(dto3);

    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.roles CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE roles_account_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    public void valueFindsAll() throws Exception {

        ProjectFilter filter = new ProjectFilter("project", List.of(ProjectStatus.DRAFT));

        mockMvc.perform(post("/api/v1/projects/filter")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(projects)));
    }

    @Test
    public void anyStatus() throws Exception {

        ProjectFilter filter = new ProjectFilter("project", null);

        mockMvc.perform(post("/api/v1/projects/filter")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(projects)));
    }

    @Test
    public void anyValue() throws Exception {

        ProjectFilter filter = new ProjectFilter(null, List.of(ProjectStatus.DRAFT));

        mockMvc.perform(post("/api/v1/projects/filter")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(projects)));
    }

    @Test
    public void nullValue() throws Exception {

        ProjectFilter filter = new ProjectFilter(null, null);

        mockMvc.perform(post("/api/v1/projects/filter")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(projects)));
    }

    @Test
    public void emptyValue() throws Exception {

        ProjectFilter filter = new ProjectFilter("", null);

        mockMvc.perform(post("/api/v1/projects/filter")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(projects)));
    }

    @Test
    public void findOne() throws Exception {

        ProjectFilter filter = new ProjectFilter("1", null);

        mockMvc.perform(post("/api/v1/projects/filter")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(List.of(dto1))));
    }

    @Test
    public void findNone() throws Exception {

        ProjectFilter filter = new ProjectFilter("17", null);

        mockMvc.perform(post("/api/v1/projects/filter")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(Collections.EMPTY_LIST)));
    }
}