package ru.borshchevskiy.pcs.entities.team;

import lombok.*;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class Team implements Serializable {

    private Long id;

    private Project project;

    private List<TeamMember> members;

}
