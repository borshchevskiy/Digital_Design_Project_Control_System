package ru.borshchevskiy.pcs.services.project;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.dto.project.ProjectFilter;

import java.util.List;

public interface ProjectService {

    ProjectDto findById(Long id);

    List<ProjectDto> findAll();

    List<ProjectDto> findAllByFilter(ProjectFilter filter);

    ProjectDto save(ProjectDto dto);

    ProjectDto deleteById(Long id);
}
