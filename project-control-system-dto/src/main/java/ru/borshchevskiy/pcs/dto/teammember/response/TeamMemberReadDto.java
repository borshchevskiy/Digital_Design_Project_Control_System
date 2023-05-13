package ru.borshchevskiy.pcs.dto.teammember.response;

import lombok.*;
import ru.borshchevskiy.pcs.enums.TeamMemberProjectRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamMemberReadDto {

    private String displayName;

    private TeamMemberProjectRole role;
}
