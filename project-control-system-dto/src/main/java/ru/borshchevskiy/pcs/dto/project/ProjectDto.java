package ru.borshchevskiy.pcs.dto.project;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.borshchevskiy.pcs.enums.ProjectStatus;

@Data
@Schema(description = "Проект")
public class ProjectDto {
    @Schema(description = "id")
    private Long id;
    @Schema(description = "Код")
    private String code;
    @Schema(description = "Название")
    private String name;
    @Schema(description = "Описание")
    private String description;
    @Schema(description = "Статус")
    private ProjectStatus status;

}
