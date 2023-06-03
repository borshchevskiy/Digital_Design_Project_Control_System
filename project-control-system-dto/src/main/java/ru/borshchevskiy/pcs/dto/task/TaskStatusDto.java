package ru.borshchevskiy.pcs.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;

@Data
@Schema(description = "Статус задачи")
public class TaskStatusDto {
    @Schema(description = "Статус")
    private TaskStatus status;
}
