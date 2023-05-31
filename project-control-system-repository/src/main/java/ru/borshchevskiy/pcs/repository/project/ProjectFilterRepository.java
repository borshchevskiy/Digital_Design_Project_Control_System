package ru.borshchevskiy.pcs.repository.project;

import ru.borshchevskiy.pcs.dto.project.ProjectFilter;
import ru.borshchevskiy.pcs.entities.project.Project;

import java.util.List;

public interface ProjectFilterRepository {

    List<Project> findAllByFilter(ProjectFilter filter);

}
