package ru.borshchevskiy.pcs.dto.task.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDto {

    private String name;

    private String description;

    private Long implementerId;

    private String laborCosts;

    private LocalDateTime deadline;

    private TaskStatus status;

    private Long authorId;

    private LocalDateTime dateCreated;

    private Long projectId;
}
