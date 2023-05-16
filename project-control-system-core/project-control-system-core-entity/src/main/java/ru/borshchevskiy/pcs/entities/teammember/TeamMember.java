package ru.borshchevskiy.pcs.entities.teammember;

import lombok.*;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.enums.TeamMemberProjectRole;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class TeamMember implements Serializable {

    private Long id;

    private Team team;

    private Employee employee;

    private TeamMemberProjectRole role;

    private List<Task> tasks;

}
