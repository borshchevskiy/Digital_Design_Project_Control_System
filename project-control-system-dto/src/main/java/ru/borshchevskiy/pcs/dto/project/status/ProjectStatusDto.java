package ru.borshchevskiy.pcs.dto.project.status;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;

@Data
@Schema(description = "Статус проекта")
public class ProjectStatusDto {

    @Schema(description = "Статус проекта")
    private ProjectStatus status;

}
