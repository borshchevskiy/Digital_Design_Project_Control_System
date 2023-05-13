package ru.borshchevskiy.pcs.entities.teammember;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.enums.TeamMemberProjectRole;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember implements Serializable {

    private Long id;

    private Team team;

    private Employee employee;

    private TeamMemberProjectRole role;

    private List<Task> tasks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember teamMember = (TeamMember) o;
        return Objects.equals(id, teamMember.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
