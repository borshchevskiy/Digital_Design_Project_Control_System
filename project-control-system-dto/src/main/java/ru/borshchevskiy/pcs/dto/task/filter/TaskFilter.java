package ru.borshchevskiy.pcs.dto.task.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;

import java.time.LocalDateTime;

@Schema(description = "Фильтр задач")
public record TaskFilter(String name,
                         TaskStatus status,
                         String implementerLastname,
                         String authorLastname,
                         LocalDateTime deadline,
                         LocalDateTime dateCreated) {
}
