package ru.borshchevskiy.pcs.dto.task;

import lombok.Data;
import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.LocalDateTime;


@Data
public class TaskDto {

    private Long id;

    private String name;

    private String description;

    private Long implementerId;

    private String laborCosts;

    private LocalDateTime deadline;

    private TaskStatus status;

    private Long authorId;

    private LocalDateTime dateCreated;

    private LocalDateTime dateUpdated;

    private Long projectId;
}
