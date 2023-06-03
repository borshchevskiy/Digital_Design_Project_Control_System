package ru.borshchevskiy.pcs.entities.project;


import jakarta.persistence.*;
import lombok.Data;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStatus status;

}
