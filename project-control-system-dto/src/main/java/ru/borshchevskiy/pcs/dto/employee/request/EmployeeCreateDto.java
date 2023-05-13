package ru.borshchevskiy.pcs.dto.employee.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateDto {

    private String firstname;

    private String lastname;

    private String patronymic;

    private String position;

    private String account;

    private String email;

    private EmployeeStatus status;
}
