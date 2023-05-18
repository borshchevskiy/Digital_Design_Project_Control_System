package ru.borshchevskiy.pcs.entities.task;


import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class Task {

    private Long id;

    private String name;

    private String description;

    private Employee implementer;

    private Duration laborCosts;

    private LocalDateTime deadline;

    private TaskStatus status;

    private Employee author;

    private LocalDateTime dateCreated;

    private LocalDateTime dateUpdated;

    private Project project;

}
