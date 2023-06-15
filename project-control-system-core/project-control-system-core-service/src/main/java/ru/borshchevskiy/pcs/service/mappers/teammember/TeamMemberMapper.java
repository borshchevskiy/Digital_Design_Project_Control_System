package ru.borshchevskiy.pcs.service.mappers.teammember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;

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
        return teamRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Team with id=" + id + " not found!"));
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));
    }

    private void copyToTeamMember(TeamMember copyTo, TeamMemberDto copyFrom) {
        copyTo.setTeam(getTeam(copyFrom.getTeamId()));
        copyTo.setEmployee(getEmployee(copyFrom.getEmployeeId()));
        copyTo.setRole(copyFrom.getRole());
    }
}
