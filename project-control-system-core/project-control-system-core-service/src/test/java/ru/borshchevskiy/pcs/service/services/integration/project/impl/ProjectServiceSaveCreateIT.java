package ru.borshchevskiy.pcs.service.services.integration.project.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class ProjectServiceSaveCreateIT extends IntegrationTestBase {


    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Project project2 = new Project();
        project2.setCode("project2");
        project2.setName("Project 2");
        project2.setStatus(ProjectStatus.DEVELOPMENT);

        projectRepository.save(project2);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void createProject() {

        final String name = "Project 1";
        final String code = "project1";

        ProjectDto createRequest = new ProjectDto();
        createRequest.setName(name);
        createRequest.setCode(code);

        ProjectDto actualResult = projectService.save(createRequest);

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getCode()).isEqualTo(code);
        assertThat(actualResult.getName()).isEqualTo(name);

    }

    @Test
    void createProjectButCodeIsTaken() {

        final String name = "Project 2";
        final String code = "project2";

        ProjectDto createRequest = new ProjectDto();
        createRequest.setName(name);
        createRequest.setCode(code);

        assertThrows(RequestDataValidationException.class, () -> projectService.save(createRequest));


    }
}