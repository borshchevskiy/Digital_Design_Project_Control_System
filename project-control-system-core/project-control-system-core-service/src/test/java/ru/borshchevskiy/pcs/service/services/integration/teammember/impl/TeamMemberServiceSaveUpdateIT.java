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
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TeamMemberServiceSaveUpdateIT extends IntegrationTestBase {


    private final TeamMemberService teamMemberService;
    private final TeamMemberRepository teamMemberRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void prepare() {
        Employee employee1 = new Employee();
        employee1.setFirstname("firstname");
        employee1.setLastname("lastname");
        employee1.setStatus(EmployeeStatus.ACTIVE);

        Employee employee2 = new Employee();
        employee2.setFirstname("firstname");
        employee2.setLastname("lastname");
        employee2.setStatus(EmployeeStatus.ACTIVE);

        Employee savedEmployee1 = employeeRepository.save(employee1);
        Employee savedEmployee2 = employeeRepository.save(employee2);

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

        Team team1 = new Team();
        team1.setProject(project1);

        Team team2 = new Team();
        team2.setProject(project2);

        teamRepository.save(team1);
        teamRepository.save(team2);

        TeamMember member1 = new TeamMember();
        member1.setEmployee(savedEmployee1);
        member1.setTeam(team1);
        member1.setRole(TeamMemberProjectRole.DEVELOPER);

        teamMemberRepository.save(member1);


    }

    @AfterEach
    public void clean() {
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
    void updateMemberRole() {

        final Long teamId = 1L;
        final Long employeeId = 1L;

        TeamMemberDto member1 = new TeamMemberDto();
        member1.setId(1L);
        member1.setTeamId(teamId);
        member1.setEmployeeId(employeeId);
        member1.setRole(TeamMemberProjectRole.PROJECT_MANAGER);

        assertTrue(teamMemberRepository.findById(employeeId).isPresent());
        assertSame(teamMemberRepository.findById(employeeId).get().getRole(), TeamMemberProjectRole.DEVELOPER);

        TeamMemberDto actualResult = teamMemberService.save(member1);

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getTeamId()).isEqualTo(teamId);
        assertThat(actualResult.getEmployeeId()).isEqualTo(employeeId);
        assertSame(actualResult.getRole(), TeamMemberProjectRole.PROJECT_MANAGER);
    }

    @Test
    void updateMembersTeam() {

        final Long newTeamId = 1L;
        final Long employeeId = 1L;

        TeamMemberDto member1 = new TeamMemberDto();
        member1.setId(1L);
        member1.setTeamId(newTeamId);
        member1.setEmployeeId(employeeId);
        member1.setRole(TeamMemberProjectRole.DEVELOPER);

        TeamMemberDto actualResult = teamMemberService.save(member1);

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
        assertThat(actualResult.getTeamId()).isEqualTo(newTeamId);
        assertThat(actualResult.getEmployeeId()).isEqualTo(employeeId);
    }

    @Test
    void updateMembersTeamButHeIsAlreadyInThatTeam() {


        final Long teamId2 = 2L;
        final Long employeeId = 1L;

        // Сохраняем сотрудника в team2
        TeamMemberDto member = new TeamMemberDto();
        member.setTeamId(teamId2);
        member.setEmployeeId(employeeId);
        member.setRole(TeamMemberProjectRole.DEVELOPER);
        teamMemberService.save(member);

        // Переводим сотрудника из team1 в team2 (но он уже есть в team2)
        member.setId(1L);
        member.setTeamId(2L);
        member.setEmployeeId(employeeId);
        member.setRole(TeamMemberProjectRole.DEVELOPER);

        assertThrows(RequestDataValidationException.class, () -> teamMemberService.save(member));
    }


    @Test
    void changeMemberInTeamButHeIsAlreadyInTeam() {


        final Long employeeId2 = 2L;
        final Long teamId = 1L;

        // Сохраняем сотрудника в team
        TeamMemberDto member = new TeamMemberDto();
        member.setTeamId(teamId);
        member.setEmployeeId(employeeId2);
        member.setRole(TeamMemberProjectRole.DEVELOPER);
        teamMemberService.save(member);

        // Заменяем первого сотрудника на второго (второй при этом уже в команде
        member.setId(1L);
        member.setTeamId(1L);
        member.setEmployeeId(employeeId2);
        member.setRole(TeamMemberProjectRole.DEVELOPER);

        assertThrows(RequestDataValidationException.class, () -> teamMemberService.save(member));

    }
}