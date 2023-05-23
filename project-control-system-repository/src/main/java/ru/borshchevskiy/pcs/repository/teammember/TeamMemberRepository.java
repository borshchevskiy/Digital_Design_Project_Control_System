package ru.borshchevskiy.pcs.repository.teammember;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
