package ru.borshchevskiy.pcs.services.team;

import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.team.TeamDto;

import java.util.List;

public interface TeamService {
    TeamDto findById(Long id);

    List<TeamDto> findAll();

    @Transactional
    TeamDto save(TeamDto dto);

    @Transactional
    boolean deleteById(Long id);
}
