package ru.borshchevskiy.pcs.entities.project;


import lombok.*;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.enums.ProjectStatus;

import java.util.List;
import java.util.Objects;

@Data
public class Project {

    private Long id;

    private String code;

    private String name;

    private String description;

    private ProjectStatus status;

    private List<Team> teams;

    private List<Task> tasks;

}
