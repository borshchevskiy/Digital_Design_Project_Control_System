package ru.borshchevskiy.pcs.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.enums.Role;

import java.util.Set;

@Data
@Schema(description = "Сотрудник")
public class EmployeeDto {
    @Schema(description = "id")
    private Long id;
    @Schema(description = "Имя")
    private String firstname;
    @Schema(description = "Фамилия")
    private String lastname;
    @Schema(description = "Отчество")
    private String patronymic;
    @Schema(description = "Должность")
    private String position;
    @Schema(description = "Учетная запись")
    private String username;
    @Schema(description = "Email")
    private String email;
    @Schema(description = "Статус")
    private EmployeeStatus status;
}
