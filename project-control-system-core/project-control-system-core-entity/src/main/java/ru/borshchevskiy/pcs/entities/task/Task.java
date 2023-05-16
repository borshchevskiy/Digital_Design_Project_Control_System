package ru.borshchevskiy.pcs.entities.task;


import lombok.*;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
public class Task {

    private Long id;

    private String name;

    private String description;

    private TeamMember implementer;

    private Duration laborCosts;

    private LocalDateTime deadline;

    private TaskStatus status;

    private TeamMember author;

    private LocalDateTime dateCreated;

    private LocalDateTime dateUpdated;

    private Project project;

}
