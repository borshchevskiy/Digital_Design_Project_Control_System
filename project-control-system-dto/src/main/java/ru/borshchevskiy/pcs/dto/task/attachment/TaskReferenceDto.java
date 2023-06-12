package ru.borshchevskiy.pcs.dto.task.attachment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Список зависимостей задачи")
public record TaskReferenceDto(@Schema(description = "Список id зависимостей задачи") List<Long> referenceIds) {
}
