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
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class ProjectServiceSaveUpdateExceptionsIT extends IntegrationTestBase {


    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Project project1 = new Project();
        project1.setCode("project1");
        project1.setName("Project1");
        project1.setStatus(ProjectStatus.DRAFT);

        Project project2 = new Project();
        project2.setCode("project2");
        project2.setName("Project2");
        project2.setStatus(ProjectStatus.DRAFT);


        projectRepository.save(project1);
        projectRepository.save(project2);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }


    @Test
    void updateProjectNotFound() {
        final Long id = Long.MIN_VALUE;

        ProjectDto requestDto = new ProjectDto();
        requestDto.setId(id);
        requestDto.setName("newName");
        requestDto.setCode("newCode");
        requestDto.setStatus(ProjectStatus.DRAFT);

        assertThrows(NotFoundException.class, () -> projectService.save(requestDto));
    }

    @Test
    void changeStatus() {
        final Long id = 1L;

        assertTrue(projectRepository.findById(id).isPresent());

        ProjectDto requestDto = new ProjectDto();
        requestDto.setId(id);
        requestDto.setName("newName");
        requestDto.setCode("newCode");
        requestDto.setStatus(null);

        assertThrows(RequestDataValidationException.class, () -> projectService.save(requestDto));
    }

    @Test
    void changeCodeButItsAlreadyTaken() {
        final Long id = 1L;

        assertTrue(projectRepository.findById(id).isPresent());

        ProjectDto requestDto = new ProjectDto();
        requestDto.setId(id);
        requestDto.setName("newName");
        requestDto.setCode("project2");
        requestDto.setStatus(ProjectStatus.DRAFT);

        assertThrows(RequestDataValidationException.class, () -> projectService.save(requestDto));
    }


}