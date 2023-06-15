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
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.mappers.project.ProjectMapper;
import ru.borshchevskiy.pcs.web.controllers.integration.IntegrationTestBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ProjectControllerCreateIT extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private Account account;
    ProjectDto dto1;
    ProjectDto dto2;
    ProjectDto dto3;
    List<ProjectDto> projects = new ArrayList<>();

    @BeforeEach
    void prepare() {

        account = new Account();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        account.setUsername("username");
        account.setPassword("password");
        account.setRoles(roles);
        accountRepository.save(account);

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
    public void create() throws Exception {

        ProjectDto projectDto = new ProjectDto();
        projectDto.setCode("project1");
        projectDto.setName("Project 1");

        ProjectDto expectedDto = new ProjectDto();
        expectedDto.setId(1L);
        expectedDto.setCode("project1");
        expectedDto.setName("Project 1");
        expectedDto.setStatus(ProjectStatus.DRAFT);

        mockMvc.perform(post("/api/v1/projects")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedDto)));
    }

    @Test
    public void codeAlreadyExists() throws Exception {

        Project project = new Project();
        project.setCode("project1");
        project.setName("Project 1");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setCode("project1");
        projectDto.setName("Project 1");

        mockMvc.perform(post("/api/v1/projects")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("Project with code " + project.getCode() + " already exists!"));
    }
}