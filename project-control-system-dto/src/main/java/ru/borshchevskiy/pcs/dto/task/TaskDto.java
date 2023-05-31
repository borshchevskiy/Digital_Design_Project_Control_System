package ru.borshchevskiy.pcs.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.LocalDateTime;


@Data
@Schema(description = "Задача")
public class TaskDto {
    @Schema(description = "id")
    private Long id;
    @Schema(description = "Название")
    private String name;
    @Schema(description = "Описание")
    private String description;
    @Schema(description = "id исполнителя")
    private Long implementerId;
    @Schema(description = "Трудозатраты")
    private String laborCosts;
    @Schema(description = "Крайний срок")
    private LocalDateTime deadline;
    @Schema(description = "Статус")
    private TaskStatus status;
    @Schema(description = "id автора")
    private Long authorId;
    @Schema(description = "Дата создания")
    private LocalDateTime dateCreated;
    @Schema(description = "Дата изменения")
    private LocalDateTime dateUpdated;
    @Schema(description = "id проекта")
    private Long projectId;
}
