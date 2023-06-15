package ru.borshchevskiy.pcs.dto.teammember;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.borshchevskiy.pcs.common.enums.TeamMemberProjectRole;

@Data
@Schema(description = "Участник команды")
public class TeamMemberDto {
    @Schema(description = "id участника")
    private Long id;
    @Schema(description = "id команды")
    private Long teamId;
    @Schema(description = "id сотрудника")
    private Long employeeId;
    @Schema(description = "Роль")
    private TeamMemberProjectRole role;

}
