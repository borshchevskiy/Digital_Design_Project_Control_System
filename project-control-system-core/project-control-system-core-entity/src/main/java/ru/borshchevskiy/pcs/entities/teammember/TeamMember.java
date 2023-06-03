package ru.borshchevskiy.pcs.entities.teammember;

import jakarta.persistence.*;
import lombok.Data;
import ru.borshchevskiy.pcs.common.enums.TeamMemberProjectRole;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.team.Team;

import java.io.Serializable;

@Data
@Entity
@Table(name = "team_members")
public class TeamMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role")
    private TeamMemberProjectRole role;

}
