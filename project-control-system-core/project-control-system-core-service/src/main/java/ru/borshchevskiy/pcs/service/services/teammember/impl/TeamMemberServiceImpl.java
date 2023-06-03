package ru.borshchevskiy.pcs.service.services.teammember.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.teammember.TeamMemberRepository;
import ru.borshchevskiy.pcs.service.mappers.teammember.TeamMemberMapper;
import ru.borshchevskiy.pcs.service.services.teammember.TeamMemberService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository repository;
    private final TeamMemberMapper teamMemberMapper;

    @Override
    @Transactional(readOnly = true)
    public TeamMemberDto findById(Long id) {
        return repository.findById(id)
                .map(teamMemberMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Team member with id=" + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberDto> findAll() {
        return repository.findAll()
                .stream()
                .map(teamMemberMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamMemberDto> findAllByTeamId(Long id) {
        return repository.findAllByTeamId(id)
                .stream()
                .map(teamMemberMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamMemberDto> findAllByProjectId(Long id) {
        return repository.findAllByProjectId(id)
                .stream()
                .map(teamMemberMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamMemberDto save(TeamMemberDto dto) {
        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private TeamMemberDto create(TeamMemberDto dto) {
        TeamMember teamMember = repository.save(teamMemberMapper.createTeamMember(dto));
        return teamMemberMapper.mapToDto(teamMember);
    }

    private TeamMemberDto update(TeamMemberDto dto) {
        TeamMember teamMember = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Team member with id=" + dto.getId() + " not found!"));

        teamMemberMapper.mergeTeamMember(teamMember, dto);
        return teamMemberMapper.mapToDto(repository.save(teamMember));
    }

    @Override
    @Transactional
    public TeamMemberDto deleteById(Long id) {
        return repository.findById(id)
                .map(teamMember -> {
                    repository.delete(teamMember);
                    return teamMember;
                })
                .map(teamMemberMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Team member with id=" + id + " not found!"));

    }


}
