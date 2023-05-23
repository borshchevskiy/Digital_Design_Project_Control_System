package ru.borshchevskiy.pcs.dto.task;

import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskFilter(String name,
                         TaskStatus status,
                         String implementerName,
                         String authorName,
                         LocalDateTime deadline,
                         LocalDateTime dateCreated) {
}
