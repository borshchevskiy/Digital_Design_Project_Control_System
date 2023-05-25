package ru.borshchevskiy.pcs.dto.teammember;

import lombok.Data;
import ru.borshchevskiy.pcs.enums.TeamMemberProjectRole;

@Data
public class TeamMemberDto {

    private Long id;

    private Long teamId;

    private Long employeeId;

    private TeamMemberProjectRole role;

}
