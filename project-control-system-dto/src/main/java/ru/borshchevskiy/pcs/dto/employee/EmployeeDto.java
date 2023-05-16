package ru.borshchevskiy.pcs.dto.employee;

import lombok.*;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;

@Data
public class EmployeeDto {

    private Long id;

    private String firstname;

    private String lastname;

    private String patronymic;

    private String position;

    private String account;

    private String email;

    private EmployeeStatus status;
}
