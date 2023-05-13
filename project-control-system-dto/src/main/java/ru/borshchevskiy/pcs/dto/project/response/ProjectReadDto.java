package ru.borshchevskiy.pcs.dto.project.response;

import lombok.*;
import ru.borshchevskiy.pcs.dto.task.response.TaskReadDto;
import ru.borshchevskiy.pcs.dto.team.response.TeamReadDto;
import ru.borshchevskiy.pcs.enums.ProjectStatus;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectReadDto {

    private Long id;

    private String code;

    private String name;

    private String description;

    private ProjectStatus status;

    private List<TeamReadDto> teams;

    private List<TaskReadDto> tasks;
}
