package ru.borshchevskiy.pcs.dto.teammember.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.borshchevskiy.pcs.enums.TeamMemberProjectRole;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberUpdateDto {

    private Long id;

    private Long teamId;

    private Long employeeId;

    private TeamMemberProjectRole role;

    private List<Long> tasks;
}
