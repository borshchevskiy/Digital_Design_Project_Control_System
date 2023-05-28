package ru.borshchevskiy.pcs.entities.employee;


import jakarta.persistence.*;
import lombok.Data;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.enums.Role;

import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@Table(name = "employees")
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "position")
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmployeeStatus status;

}

