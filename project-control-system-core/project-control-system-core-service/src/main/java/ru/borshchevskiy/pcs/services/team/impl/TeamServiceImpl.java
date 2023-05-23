package ru.borshchevskiy.pcs.services.team.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.mappers.team.TeamMapper;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.services.team.TeamService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository repository;
    private final TeamMapper teamMapper;

    @Override
    public TeamDto findById(Long id) {
        return repository.findById(id)
                .map(teamMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Team with id=" + id + " not found!"));
    }

    @Override
    public List<TeamDto> findAll() {
        return repository.findAll()
                .stream()
                .map(teamMapper::mapToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public TeamDto save(TeamDto dto) {
        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private TeamDto create(TeamDto dto) {
        Team team = repository.save(teamMapper.createTeam(dto));
        return teamMapper.mapToDto(team);
    }

    private TeamDto update(TeamDto dto) {
        Team team = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Team with id=" + dto.getId() + " not found!"));

        teamMapper.mergeTeam(team, dto);
        return teamMapper.mapToDto(repository.save(team));
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return repository.findById(id)
                .map(project -> {
                    repository.delete(project);
                    return true;
                })
                .orElse(false);
    }
}
