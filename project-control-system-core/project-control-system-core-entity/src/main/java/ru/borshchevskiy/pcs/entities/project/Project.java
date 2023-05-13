package ru.borshchevskiy.pcs.entities.project;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.team.Team;
import ru.borshchevskiy.pcs.enums.ProjectStatus;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    private Long id;

    private String code;

    private String name;

    private String description;

    private ProjectStatus status;

    private List<Team> teams;

    private List<Task> tasks;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(code, project.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
