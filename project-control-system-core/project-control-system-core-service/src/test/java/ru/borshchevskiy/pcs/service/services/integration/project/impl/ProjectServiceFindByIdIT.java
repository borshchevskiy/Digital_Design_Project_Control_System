package ru.borshchevskiy.pcs.service.services.integration.project.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class ProjectServiceFindByIdIT extends IntegrationTestBase {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepareTestData() {
        Project project = new Project();
        project.setCode("project1");
        project.setName("Project 1");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);
    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void projectExists() {
        final long expectedId = 1L;

        ProjectDto projectById = projectService.findById(expectedId);

        assertThat(projectById.getId()).isEqualTo(expectedId);
    }

    @Test
    void projectDoesntExists() {
        final long expectedId = Long.MIN_VALUE;

        assertThrows(NotFoundException.class, () -> projectService.findById(expectedId));
    }

}
