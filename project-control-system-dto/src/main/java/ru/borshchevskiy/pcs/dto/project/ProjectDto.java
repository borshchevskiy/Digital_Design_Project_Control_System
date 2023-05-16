package ru.borshchevskiy.pcs.dto.project;


import lombok.*;
import ru.borshchevskiy.pcs.enums.ProjectStatus;

import java.util.List;

@Data
public class ProjectDto {

    private Long id;

    private String code;

    private String name;

    private String description;

    private ProjectStatus status;

    private List<Long> teams;

    private List<Long> tasks;


}
