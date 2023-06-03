package ru.borshchevskiy.pcs.service.mappers.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeamMapper {

    private final ProjectRepository projectRepository;

    public TeamDto mapToDto(Team team) {
        TeamDto teamDto = new TeamDto();

        teamDto.setId(team.getId());
        teamDto.setProjectId(team.getProject().getId());

        return teamDto;
    }

    public Team createTeam(TeamDto dto) {
        Team team = new Team();

        copyToTeam(team, dto);

        return team;
    }

    public void mergeTeam(Team team, TeamDto dto) {

        copyToTeam(team, dto);
    }

    private Project getProject(Long id) {
        return Optional.ofNullable(id)
                .flatMap(projectRepository::findById)
                .orElse(null);
    }

    private void copyToTeam(Team copyTo, TeamDto copyFrom) {
        copyTo.setProject(getProject(copyFrom.getProjectId()));
    }
}
