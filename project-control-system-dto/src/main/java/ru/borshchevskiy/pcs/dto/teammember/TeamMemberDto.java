package ru.borshchevskiy.pcs.dto.teammember;

import lombok.*;
import ru.borshchevskiy.pcs.enums.TeamMemberProjectRole;

import java.util.List;

@Data
public class TeamMemberDto {

    private Long id;

    private Long teamId;

    private Long employeeId;

    private TeamMemberProjectRole role;

    private List<Long> tasks;
}
