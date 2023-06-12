package ru.borshchevskiy.pcs.service.services.integration.teammember.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.enums.TeamMemberProjectRole;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.repository.teammember.TeamMemberRepository;
import ru.borshchevskiy.pcs.service.services.email.EmailService;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.teammember.TeamMemberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TeamMemberServiceDeleteIT extends IntegrationTestBase {


    private final TeamMemberService teamMemberService;
    private final EmailService emailService;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepare() {
        Employee employee1 = new Employee();
        employee1.setFirstname("firstname");
        employee1.setLastname("lastname");
        employee1.setStatus(EmployeeStatus.ACTIVE);

        Employee savedEmployee1 = employeeRepository.save(employee1);

        Project project1 = new Project();
        project1.setCode("project1");
        project1.setName("Project 1");
        project1.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project1);

        Team team1 = new Team();
        team1.setProject(project1);

        teamRepository.save(team1);

        TeamMember member1 = new TeamMember();
        member1.setEmployee(savedEmployee1);
        member1.setTeam(team1);
        member1.setRole(TeamMemberProjectRole.DEVELOPER);

        teamMemberRepository.saveAndFlush(member1);

    }

    @AfterEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.team_members CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.teams CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.projects CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE team_members_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE teams_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE projects_id_seq RESTART");
    }

    @Test
    void deleteSuccess() {

        final Long id = 1L;

        assertTrue(teamMemberRepository.findById(id).isPresent());

        TeamMemberDto actualResult = teamMemberService.deleteById(id);

        assertNotNull(actualResult);
        assertThat(actualResult.getId()).isEqualTo(id);
        assertTrue(teamMemberRepository.findById(id).isEmpty());
    }


    @Test
    void projectNotFound() {
        final Long id = Long.MIN_VALUE;

        assertThrows(NotFoundException.class, () -> teamMemberService.deleteById(id));
    }

}