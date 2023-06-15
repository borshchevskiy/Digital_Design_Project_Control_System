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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TeamServiceDeleteIT extends IntegrationTestBase {


    private final TeamService teamService;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Project project1 = new Project();
        project1.setCode("project1");
        project1.setName("Project 1");
        project1.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project1);

        Team team1 = new Team();
        team1.setProject(project1);

        teamRepository.save(team1);
    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.teams CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE teams_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void deleteSuccess() {

        final Long id = 1L;

        assertTrue(teamRepository.findById(id).isPresent());

        TeamDto actualResult = teamService.deleteById(id);

        assertNotNull(actualResult);
        assertThat(actualResult.getId()).isEqualTo(id);
        assertTrue(teamRepository.findById(id).isEmpty());
    }


    @Test
    void projectNotFound() {
        final Long id = Long.MIN_VALUE;

        assertThrows(NotFoundException.class, () -> teamService.deleteById(id));
    }

}