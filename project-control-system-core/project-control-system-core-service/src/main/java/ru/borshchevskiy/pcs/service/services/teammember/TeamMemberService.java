package ru.borshchevskiy.pcs.service.services.teammember;

import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;

import java.util.List;

public interface TeamMemberService {
    TeamMemberDto findById(Long id);

    List<TeamMemberDto> findAll();


    TeamMemberDto save(TeamMemberDto dto);

    TeamMemberDto deleteById(Long id);

    List<TeamMemberDto> findAllByTeamId(Long id);

    List<TeamMemberDto> findAllByProjectId(Long id);

}
