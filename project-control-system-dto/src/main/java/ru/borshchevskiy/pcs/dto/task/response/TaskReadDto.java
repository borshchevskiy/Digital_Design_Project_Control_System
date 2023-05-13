package ru.borshchevskiy.pcs.dto.task.response;

import lombok.*;
import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskReadDto {

    private Long id;

    private String name;

    private String description;

    private String implementerDisplayName;

    private String laborCosts;

    private LocalDateTime deadline;

    private TaskStatus status;

    private String authorDisplayName;

    private LocalDateTime dateCreated;

    private LocalDateTime dateUpdated;

    private String projectCode;

    private String projectName;
}
