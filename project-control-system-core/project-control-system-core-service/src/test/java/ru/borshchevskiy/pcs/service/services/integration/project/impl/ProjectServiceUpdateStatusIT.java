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
import ru.borshchevskiy.pcs.common.exceptions.StatusModificationException;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.dto.project.ProjectStatusDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class ProjectServiceUpdateStatusIT extends IntegrationTestBase {


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
    void updateStatus() {

        final Long id = 1L;

        assertTrue(projectRepository.findById(id).isPresent());

        ProjectStatusDto statusDto = new ProjectStatusDto();
        statusDto.setStatus(ProjectStatus.DEVELOPMENT);


        ProjectDto actualResult = projectService.updateStatus(id, statusDto);

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getStatus()).isEqualTo(ProjectStatus.DEVELOPMENT);
    }

    @Test
    void updateStatusIncorrect() {

        final Long id = 1L;

        assertTrue(projectRepository.findById(id).isPresent());

        ProjectStatusDto statusDto = new ProjectStatusDto();
        statusDto.setStatus(ProjectStatus.COMPLETED);

        assertThrows(StatusModificationException.class, () -> projectService.updateStatus(id, statusDto));
    }

    @Test
    void projectNotFound() {

        ProjectStatusDto statusDto = new ProjectStatusDto();
        statusDto.setStatus(ProjectStatus.DEVELOPMENT);

        assertThrows(NotFoundException.class, () -> projectService.updateStatus(Long.MIN_VALUE, statusDto));
    }

    @Test
    void updateFinalStatus() {

        Project project2 = new Project();
        project2.setCode("project2");
        project2.setName("Project 2");
        project2.setStatus(ProjectStatus.COMPLETED);

        Project project1 = projectRepository.save(project2);

        assertTrue(projectRepository.findById(project1.getId()).isPresent());

        ProjectStatusDto statusDto = new ProjectStatusDto();
        statusDto.setStatus(ProjectStatus.DEVELOPMENT);

        assertThrows(StatusModificationException.class, () -> projectService.updateStatus(project1.getId(), statusDto));
    }
}