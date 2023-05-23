package ru.borshchevskiy.pcs.mappers.teammember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeamMemberMapper {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    public TeamMemberDto mapToDto(TeamMember teamMember) {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setId(teamMember.getId());
        teamMemberDto.setTeamId(teamMember.getTeam().getId());
        teamMemberDto.setEmployeeId(teamMember.getEmployee().getId());
        teamMemberDto.setRole(teamMember.getRole());
        return teamMemberDto;
    }

    public TeamMember createTeamMember(TeamMemberDto dto) {
        TeamMember teamMember = new TeamMember();

        copyToTeamMember(teamMember, dto);

        return teamMember;
    }

    public void mergeTeamMember(TeamMember team, TeamMemberDto dto) {

        copyToTeamMember(team, dto);
    }

    private Team getTeam(Long id) {
        return Optional.ofNullable(id)
                .flatMap(teamRepository::findById)
                .orElse(null);
    }

    private Employee getEmployee(Long id) {
        return Optional.ofNullable(id)
                .flatMap(employeeRepository::findById)
                .orElse(null);
    }

    private void copyToTeamMember(TeamMember copyTo, TeamMemberDto copyFrom) {
        copyTo.setTeam(getTeam(copyFrom.getTeamId()));
        copyTo.setEmployee(getEmployee(copyFrom.getEmployeeId()));
        copyTo.setRole(copyFrom.getRole());
    }
}
