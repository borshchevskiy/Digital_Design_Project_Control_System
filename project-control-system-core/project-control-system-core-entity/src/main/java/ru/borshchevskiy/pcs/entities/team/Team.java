package ru.borshchevskiy.pcs.entities.team;

import jakarta.persistence.*;
import lombok.Data;
import ru.borshchevskiy.pcs.entities.project.Project;

import java.io.Serializable;

@Data
@Entity
@Table(name = "teams")
public class Team implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private Project project;

}
