package ru.borshchevskiy.pcs.entities.team;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;

import java.io.Serializable;
import java.util.List;

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
