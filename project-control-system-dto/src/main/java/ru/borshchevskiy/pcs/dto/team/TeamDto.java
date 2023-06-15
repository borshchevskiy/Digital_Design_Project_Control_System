package ru.borshchevskiy.pcs.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Команда")
public class TeamDto {
    @Schema(description = "id команды")
    private Long id;
    @Schema(description = "id проекта")
    private Long projectId;

}
