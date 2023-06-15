package ru.borshchevskiy.pcs.service.services.teammember.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.team.TeamRepository;
import ru.borshchevskiy.pcs.repository.teammember.TeamMemberRepository;
import ru.borshchevskiy.pcs.service.mappers.teammember.TeamMemberMapper;
import ru.borshchevskiy.pcs.service.services.teammember.TeamMemberService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository repository;
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
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

        if (dto.getTeamId() == null ||
            dto.getEmployeeId() == null ||
            dto.getRole() == null) {
            throw new RequestDataValidationException("Team, employee and role must be specified!");
        }

        if (teamRepository.findById(dto.getTeamId()).isEmpty()) {
            throw new NotFoundException("Specified team not found!");
        }

        if (employeeRepository.findById(dto.getEmployeeId()).isEmpty()) {
            throw new NotFoundException("Specified employee not found!");
        }

        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private TeamMemberDto create(TeamMemberDto dto) {

        // Проверяем если Employee уже состоит в этой команде, то нельзя сделать его участником этой же команды еще раз
        checkIfAlreadyMember(dto);

        TeamMember teamMember = repository.save(teamMemberMapper.createTeamMember(dto));
        return teamMemberMapper.mapToDto(teamMember);
    }

    private TeamMemberDto update(TeamMemberDto dto) {

        TeamMember teamMember = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Team member with id=" + dto.getId() + " not found!"));

        // При замене команды либо при замене сотрудника, проверяем не состоит ли уже этот сотрудник в этой команде
        if (!teamMember.getTeam().getId().equals(dto.getTeamId())
            || !teamMember.getEmployee().getId().equals(dto.getEmployeeId())) {

            checkIfAlreadyMember(dto);
        }

        teamMemberMapper.mergeTeamMember(teamMember, dto);

        return teamMemberMapper.mapToDto(repository.save(teamMember));
    }

    // Метод проверяет, состоит ли указанный в запросе Employee в указанной в запросе команде
    private void checkIfAlreadyMember(TeamMemberDto dto) {
        repository.findAllByTeamId(dto.getTeamId()).stream()
                .map(TeamMember::getEmployee)
                .map(Employee::getId)
                .filter(e -> e.equals(dto.getEmployeeId()))
                .findAny()
                .ifPresent((id) -> {
                    throw new RequestDataValidationException("Employee with id=" + id +
                                                             " is already a member of the team!");
                });
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
