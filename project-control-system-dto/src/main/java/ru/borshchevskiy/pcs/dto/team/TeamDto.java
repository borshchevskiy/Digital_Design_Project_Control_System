package ru.borshchevskiy.pcs.dto.team;

import lombok.*;

import java.util.List;

@Data
public class TeamDto {

    private Long id;

    private Long projectId;

    private List<Long> members;
}
