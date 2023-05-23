package ru.borshchevskiy.pcs.dto.project;


import lombok.Data;

import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.enums.ProjectStatus;

import java.util.List;

@Data
public class ProjectDto {

    private Long id;

    private String code;

    private String name;

    private String description;

    private ProjectStatus status;

}
