package ru.borshchevskiy.pcs.dto.project.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.borshchevskiy.pcs.enums.ProjectStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDto {

    private String code;

    private String name;

    private String description;

    private ProjectStatus status;
}
