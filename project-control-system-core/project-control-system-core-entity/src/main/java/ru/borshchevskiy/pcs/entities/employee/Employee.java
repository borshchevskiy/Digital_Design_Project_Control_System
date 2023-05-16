package ru.borshchevskiy.pcs.entities.employee;


import lombok.*;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;

import java.io.Serializable;
import java.util.Objects;

@Data
public class Employee implements Serializable {

    private Long id;

    private String firstname;

    private String lastname;

    private String patronymic;

    private String position;

    private String account;

    private String email;

    private EmployeeStatus status;

}
