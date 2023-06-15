package ru.borshchevskiy.pcs.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borshchevskiy.pcs.entities.team.Team;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByProjectId(Long id);
}
