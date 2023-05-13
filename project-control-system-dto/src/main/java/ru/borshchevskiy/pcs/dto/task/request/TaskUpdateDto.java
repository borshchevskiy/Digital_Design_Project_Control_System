package ru.borshchevskiy.pcs.dto.task.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDto {

    private Long id;

    private String name;

    private String description;

    private Long implementerId;

    private String laborCosts;

    private Long authorId;

    private Long projectId;
}
