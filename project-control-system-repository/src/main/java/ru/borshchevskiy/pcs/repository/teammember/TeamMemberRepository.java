package ru.borshchevskiy.pcs.repository.teammember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findAllByTeamId(Long id);

    @Query("SELECT t FROM TeamMember t JOIN Team team WHERE team.project.id = :id")
    List<TeamMember> findAllByProjectId(@Param("id") Long id);
}
