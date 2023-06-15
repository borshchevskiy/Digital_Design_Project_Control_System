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
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.teammember.TeamMemberService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TeamMemberServiceSaveIT extends IntegrationTestBase {


    private final TeamMemberService teamMemberService;
    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void prepare() {
        Employee employee = new Employee();
        employee.setFirstname("firstname");
        employee.setLastname("lastname");
        employee.setStatus(EmployeeStatus.ACTIVE);

        employeeRepository.save(employee);

        Project project = new Project();
        project.setCode("project");
        project.setName("Project");
        project.setStatus(ProjectStatus.DRAFT);

        projectRepository.save(project);

        Team team = new Team();
        team.setProject(project);

        teamRepository.save(team);

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
    void teamIdIsEmpty() {

        TeamMemberDto createRequest = new TeamMemberDto();
        createRequest.setRole(TeamMemberProjectRole.DEVELOPER);
        createRequest.setEmployeeId(1L);

        assertThrows(RequestDataValidationException.class, () -> teamMemberService.save(createRequest));
    }

    @Test
    void employeeIdIsEmpty() {

        TeamMemberDto createRequest = new TeamMemberDto();
        createRequest.setRole(TeamMemberProjectRole.DEVELOPER);
        createRequest.setTeamId(1L);

        assertThrows(RequestDataValidationException.class, () -> teamMemberService.save(createRequest));
    }

    @Test
    void roleIsEmpty() {

        TeamMemberDto createRequest = new TeamMemberDto();
        createRequest.setEmployeeId(1L);
        createRequest.setTeamId(1L);

        assertThrows(RequestDataValidationException.class, () -> teamMemberService.save(createRequest));
    }

    @Test
    void teamNotFound() {

        TeamMemberDto createRequest = new TeamMemberDto();
        createRequest.setEmployeeId(1L);
        createRequest.setTeamId(Long.MIN_VALUE);
        createRequest.setRole(TeamMemberProjectRole.DEVELOPER);

        assertThrows(NotFoundException.class, () -> teamMemberService.save(createRequest));
    }

    @Test
    void employeeNotFound() {

        TeamMemberDto createRequest = new TeamMemberDto();
        createRequest.setEmployeeId(Long.MIN_VALUE);
        createRequest.setTeamId(1L);
        createRequest.setRole(TeamMemberProjectRole.DEVELOPER);

        assertThrows(NotFoundException.class, () -> teamMemberService.save(createRequest));
    }

}