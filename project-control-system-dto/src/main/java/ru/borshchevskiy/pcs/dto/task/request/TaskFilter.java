package ru.borshchevskiy.pcs.dto.task.request;

import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskFilter(String name,
                         TaskStatus status,
                         Long implementerId,
                         Long authorId,
                         LocalDateTime deadline,
                         LocalDateTime dateCreated) {
}
