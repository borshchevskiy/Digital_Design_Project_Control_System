package ru.borshchevskiy.pcs.services.teammember;

import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;

import java.util.List;

public interface TeamMemberService {
    TeamMemberDto findById(Long id);

    List<TeamMemberDto> findAll();


    TeamMemberDto save(TeamMemberDto dto);


    TeamMemberDto deleteById(Long id);
}
