package ru.borshchevskiy.pcs.dto.project;

import ru.borshchevskiy.pcs.enums.ProjectStatus;

import java.util.List;

public record ProjectFilter(String value,
                            List<ProjectStatus> statuses) {
}
