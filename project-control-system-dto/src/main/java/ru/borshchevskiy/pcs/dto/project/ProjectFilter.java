package ru.borshchevskiy.pcs.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.borshchevskiy.pcs.enums.ProjectStatus;

import java.util.List;

@Schema(description = "Фильтр проектов")
public record ProjectFilter(@Schema(description = "Текстовое значение") String value,
                            @Schema(description = "Статус") List<ProjectStatus> statuses) {
}
