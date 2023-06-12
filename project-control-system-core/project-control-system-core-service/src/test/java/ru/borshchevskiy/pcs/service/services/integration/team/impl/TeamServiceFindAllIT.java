package ru.borshchevskiy.pcs.service.services.integration.team.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.team.TeamService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TeamServiceFindAllIT extends IntegrationTestBase {


    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void prepare() {
        Project project1 = new Project();
        project1.setCode("project1");
        project1.setName("Project 1");
        project1.setStatus(ProjectStatus.DRAFT);

        Project project2 = new Project();
        project2.setCode("project2");
        project2.setName("Project 2");
        project2.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project1);
        projectRepository.save(project2);

        Team team1 = new Team();
        team1.setProject(project1);

        Team team2 = new Team();
        team2.setProject(project2);

        teamRepository.save(team1);
        teamRepository.save(team2);
    }

    @AfterEach
    public void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.teams CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE teams_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void teamsExist() {

        List<TeamDto> all = teamService.findAll();

        assertNotNull(all);
        assertThat(all.size()).isEqualTo(2L);
    }

}