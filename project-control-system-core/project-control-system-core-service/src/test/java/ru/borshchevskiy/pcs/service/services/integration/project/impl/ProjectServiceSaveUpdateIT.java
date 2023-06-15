package ru.borshchevskiy.pcs.service.services.integration.project.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class ProjectServiceSaveUpdateIT extends IntegrationTestBase {


    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Project project = new Project();
        project.setCode("project");
        project.setName("Project");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);

    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void updateProject() {

        final Long id = 1L;

        assertTrue(projectRepository.findById(id).isPresent());

        final String newName = "newName";
        final String newCode = "newCode";

        ProjectDto updateRequest = new ProjectDto();
        updateRequest.setId(id);
        updateRequest.setName(newName);
        updateRequest.setCode(newCode);
        updateRequest.setStatus(ProjectStatus.DRAFT);

        ProjectDto actualResult = projectService.save(updateRequest);

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getName()).isEqualTo(newName);
        assertThat(actualResult.getCode()).isEqualTo(newCode);
    }

}