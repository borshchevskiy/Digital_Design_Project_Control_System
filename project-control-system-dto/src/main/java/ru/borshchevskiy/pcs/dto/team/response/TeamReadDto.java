package ru.borshchevskiy.pcs.dto.team.response;

import lombok.*;
import ru.borshchevskiy.pcs.dto.teammember.response.TeamMemberReadDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamReadDto {

    private String projectCode;

    private List<TeamMemberReadDto> members;
}
