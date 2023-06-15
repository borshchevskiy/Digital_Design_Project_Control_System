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
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.teammember.TeamMemberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class TeamMemberServiceFindByIdIT extends IntegrationTestBase {

    private final TeamMemberService teamMemberService;
    private final TeamMemberRepository teamMemberRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void prepareTestData() {
        Employee employee = new Employee();
        employee.setFirstname("firstname");
        employee.setLastname("lastname");
        employee.setStatus(EmployeeStatus.ACTIVE);

        Employee savedEmployee = employeeRepository.save(employee);

        Project project = new Project();
        project.setCode("project");
        project.setName("Project");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);

        Team team = new Team();
        team.setProject(project);

        teamRepository.save(team);

        TeamMember member = new TeamMember();
        member.setEmployee(savedEmployee);
        member.setTeam(team);
        member.setRole(TeamMemberProjectRole.DEVELOPER);

        teamMemberRepository.save(member);
    }

    @AfterEach
    void cleanDatabase() {
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
    void teamMemberExists() {
        final long expectedId = 1L;

        TeamMemberDto teamMemberById = teamMemberService.findById(expectedId);

        assertThat(teamMemberById.getId()).isEqualTo(expectedId);
    }

    @Test
    void teamMemberDoesntExists() {
        final long expectedId = Long.MIN_VALUE;

        assertThrows(NotFoundException.class, () -> teamMemberService.findById(expectedId));
    }

}
