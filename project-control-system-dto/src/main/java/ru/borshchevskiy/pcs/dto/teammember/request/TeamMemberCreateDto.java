package ru.borshchevskiy.pcs.dto.teammember.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.borshchevskiy.pcs.enums.TeamMemberProjectRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberCreateDto {

    private Long teamId;

    private Long employeeId;

    private TeamMemberProjectRole role;
}
