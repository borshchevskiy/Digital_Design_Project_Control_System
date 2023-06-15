package ru.borshchevskiy.pcs.service.services.integration.team.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.team.TeamService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class TeamServiceFindByIdIT extends IntegrationTestBase {

    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepareTestData() {
        Project project = new Project();
        project.setCode("project1");
        project.setName("Project 1");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);

        Team team = new Team();
        team.setProject(project);

        teamRepository.save(team);
    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.teams CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE teams_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void teamExists() {
        final long expectedId = 1L;

        TeamDto teamById = teamService.findById(expectedId);

        assertThat(teamById.getId()).isEqualTo(expectedId);
    }

    @Test
    void teamDoesntExists() {
        final long expectedId = Long.MIN_VALUE;

        assertThrows(NotFoundException.class, () -> teamService.findById(expectedId));
    }

}
