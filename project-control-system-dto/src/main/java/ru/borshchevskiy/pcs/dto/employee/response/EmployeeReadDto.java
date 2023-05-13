package ru.borshchevskiy.pcs.dto.employee.response;

import lombok.*;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeReadDto {

    private Long id;

    private String firstname;

    private String lastname;

    private String patronymic;

    private String displayName;

    private String position;

    private String account;

    private String email;

    private EmployeeStatus status;
}
