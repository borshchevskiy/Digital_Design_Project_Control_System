package ru.borshchevskiy.pcs.dto.task;

import ru.borshchevskiy.pcs.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskFilter(String name,
                         TaskStatus status,
                         String implementerLastname,
                         String authorLastname,
                         LocalDateTime deadline,
                         LocalDateTime dateCreated) {
}
