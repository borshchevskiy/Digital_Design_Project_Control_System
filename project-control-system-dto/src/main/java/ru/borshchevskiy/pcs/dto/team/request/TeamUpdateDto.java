package ru.borshchevskiy.pcs.dto.team.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateDto {

    private Long id;

    private Long projectId;

    private List<Long> members;
}
