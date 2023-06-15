package ru.borshchevskiy.pcs.service.services.team.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.service.mappers.team.TeamMapper;
import ru.borshchevskiy.pcs.service.services.team.TeamService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository repository;
    private final ProjectRepository projectRepository;
    private final TeamMapper teamMapper;

    @Override
    @Transactional(readOnly = true)
    public TeamDto findById(Long id) {
        return repository.findById(id)
                .map(teamMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Team with id=" + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDto> findAll() {
        return repository.findAll()
                .stream()
                .map(teamMapper::mapToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public TeamDto save(TeamDto dto) {

        if (dto.getProjectId() == null) {
            throw new RequestDataValidationException("Project must be specified!");
        }

        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private TeamDto create(TeamDto dto) {
        // При создании проверяем не назначена ли уже команда на проект из запроса
        if (repository.findByProjectId(dto.getProjectId()).isPresent()) {
            throw new RequestDataValidationException("Project id=" + dto.getProjectId() + " already has a team.");
        }

        Team team = repository.save(teamMapper.createTeam(dto));
        return teamMapper.mapToDto(team);
    }

    private TeamDto update(TeamDto dto) {

        Team team = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Team with id=" + dto.getId() + " not found!"));

        // При обновлении (смене проекта) проверяем есть ли уже команда у нового проекта
        if (!dto.getProjectId().equals(team.getProject().getId()) &&
            repository.findByProjectId(dto.getProjectId()).isPresent()) {
            throw new RequestDataValidationException("Project id=" + dto.getProjectId() + " already has a team.");
        }

        teamMapper.mergeTeam(team, dto);

        return teamMapper.mapToDto(repository.save(team));
    }

    @Override
    @Transactional
    public TeamDto deleteById(Long id) {
        return repository.findById(id)
                .map(team -> {
                    repository.delete(team);
                    return team;
                })
                .map(teamMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Team with id=" + id + " not found!"));

    }
}
