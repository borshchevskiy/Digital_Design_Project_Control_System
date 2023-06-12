package ru.borshchevskiy.pcs.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.borshchevskiy.pcs.common.enums.Role;

import java.util.Set;

@Data
@Schema(description = "Учетная запись")
public class AccountDto {
    @Schema(description = "id")
    private Long id;
    @Schema(description = "Учетная запись")
    private String username;
    @Schema(description = "Пароль")
    private String password;
    @Schema(description = "Роли")
    private Set<Role> roles;
}
