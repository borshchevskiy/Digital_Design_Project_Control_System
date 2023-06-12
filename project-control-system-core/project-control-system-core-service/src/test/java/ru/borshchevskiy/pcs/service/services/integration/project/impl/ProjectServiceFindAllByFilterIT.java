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
import ru.borshchevskiy.pcs.dto.project.ProjectFilter;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class ProjectServiceFindAllByFilterIT extends IntegrationTestBase {


    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;


    @BeforeEach
    void prepare() {
        Project project1 = new Project();
        project1.setCode("project1");
        project1.setName("Project 1");
        project1.setStatus(ProjectStatus.DRAFT);

        Project project2 = new Project();
        project2.setCode("project2");
        project2.setName("Project 2");
        project2.setStatus(ProjectStatus.DEVELOPMENT);

        projectRepository.save(project1);
        projectRepository.save(project2);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void findAll() {

        ProjectFilter projectFilter = new ProjectFilter("project", List.of(ProjectStatus.DRAFT, ProjectStatus.DEVELOPMENT));

        List<ProjectDto> all = projectService.findAllByFilter(projectFilter);

        assertNotNull(all);
        assertThat(all.size()).isEqualTo(2L);
    }

    @Test
    void findOne() {

        ProjectFilter projectFilter = new ProjectFilter("1", List.of(ProjectStatus.DRAFT, ProjectStatus.DEVELOPMENT));

        List<ProjectDto> all = projectService.findAllByFilter(projectFilter);

        assertNotNull(all);
        assertThat(all.size()).isEqualTo(1);
    }

    @Test
    void findNone() {

        ProjectFilter projectFilter = new ProjectFilter("project", List.of(ProjectStatus.TESTING, ProjectStatus.COMPLETED));

        List<ProjectDto> all = projectService.findAllByFilter(projectFilter);

        assertNotNull(all);
        assertThat(all).isEmpty();
    }


}