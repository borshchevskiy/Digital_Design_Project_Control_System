package ru.borshchevskiy.pcs.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Учетная запись и пароль для входа")
public record LoginDto(@Schema(description = "Учетная запись") String username,
                       @Schema(description = "Пароль") String password) {


}
