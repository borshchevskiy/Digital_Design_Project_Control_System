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
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.team.TeamService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TeamServiceSaveUpdateIT extends IntegrationTestBase {


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

        Project project2 = new Project();
        project2.setCode("project2");
        project2.setName("Project 2");
        project2.setStatus(ProjectStatus.DRAFT);

        Project project3 = new Project();
        project3.setCode("project3");
        project3.setName("Project 3");
        project3.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        Team team1 = new Team();
        team1.setProject(project1);

        Team team3 = new Team();
        team3.setProject(project3);

        teamRepository.save(team1);
        teamRepository.save(team3);

    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.teams CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE teams_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void updateTeam() {

        final Long taskId = 1L;
        final Long newProjectId = 2L;

        TeamDto updateRequest = new TeamDto();
        updateRequest.setId(taskId);
        updateRequest.setProjectId(newProjectId);

        TeamDto actualResult = teamService.save(updateRequest);

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getProjectId()).isEqualTo(newProjectId);
    }

    @Test
    void teamNotFound() {

        final Long taskId = Long.MIN_VALUE;
        final Long newProjectId = 2L;

        TeamDto updateRequest = new TeamDto();
        updateRequest.setId(taskId);
        updateRequest.setProjectId(newProjectId);

        assertThrows(NotFoundException.class, () -> teamService.save(updateRequest));
    }

    @Test
    void projectAlreadyHasTeam() {

        final Long taskId = 1L;
        final Long newProjectId = 3L;

        TeamDto updateRequest = new TeamDto();
        updateRequest.setId(taskId);
        updateRequest.setProjectId(newProjectId);

        assertThrows(RequestDataValidationException.class, () -> teamService.save(updateRequest));
    }
}