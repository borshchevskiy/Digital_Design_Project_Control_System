package ru.borshchevskiy.pcs.service.services.project;

import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.dto.project.filter.ProjectFilter;
import ru.borshchevskiy.pcs.dto.project.status.ProjectStatusDto;

import java.util.List;

public interface ProjectService {

    ProjectDto findById(Long id);

    List<ProjectDto> findAll();

    List<ProjectDto> findAllByFilter(ProjectFilter filter);

    ProjectDto save(ProjectDto dto);

    ProjectDto deleteById(Long id);

    ProjectDto updateStatus(Long id, ProjectStatusDto request);

}
