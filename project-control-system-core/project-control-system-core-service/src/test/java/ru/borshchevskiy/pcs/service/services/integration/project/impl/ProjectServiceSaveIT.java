package ru.borshchevskiy.pcs.service.services.integration.project.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class ProjectServiceSaveIT extends IntegrationTestBase {


    private final ProjectService projectService;
    private final JdbcTemplate jdbcTemplate;


    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void createButProjectCodeIsEmpty() {

        ProjectDto createRequest = new ProjectDto();
        createRequest.setName("Project 1");

        assertThrows(RequestDataValidationException.class, () -> projectService.save(createRequest));
    }

    @Test
    void createButProjectNameIsEmpty() {

        ProjectDto createRequest = new ProjectDto();
        createRequest.setCode("project1");

        assertThrows(RequestDataValidationException.class, () -> projectService.save(createRequest));
    }
}