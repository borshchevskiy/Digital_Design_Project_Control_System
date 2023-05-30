package ru.borshchevskiy.pcs.service.services.project.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.dto.project.ProjectFilter;
import ru.borshchevskiy.pcs.dto.project.ProjectStatusDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.enums.ProjectStatus;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.exceptions.StatusModificationException;
import ru.borshchevskiy.pcs.service.mappers.project.ProjectMapper;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectSpecificationUtil;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public ProjectDto findById(Long id) {
        return repository.findById(id)
                .map(projectMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Project with id=" + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findAll() {
        return repository.findAll()
                .stream()
                .map(projectMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findAllByFilter(ProjectFilter filter) {
        return repository.findAll(ProjectSpecificationUtil.getSpecification(filter))
                .stream()
                .map(projectMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto save(ProjectDto dto) {
        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private ProjectDto create(ProjectDto dto) {
        Project project = repository.save(projectMapper.createProject(dto));

        return projectMapper.mapToDto(project);
    }

    private ProjectDto update(ProjectDto dto) {
        Project project = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Project with id=" + dto.getId() + " not found!"));

        projectMapper.mergeProject(project, dto);

        return projectMapper.mapToDto(repository.save(project));
    }

    @Override
    @Transactional
    public ProjectDto deleteById(Long id) {
        return repository.findById(id)
                .map(project -> {
                    repository.delete(project);
                    return project;
                })
                .map(projectMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Project with id=" + id + " not found!"));

    }

    @Override
    @Transactional
    public ProjectDto updateStatus(Long id, ProjectStatusDto request) {
        return repository.findById(id)
                .map(project -> {
                    ProjectStatus currentStatus = project.getStatus();
                    ProjectStatus newStatus = request.getStatus();

                    // Изменение статуса возможно только на следующий по цепочке, нельзя выставить предыдщий статус или
                    // перескочить через один.
                    if (newStatus.ordinal() - currentStatus.ordinal() != 1) {
                        String exceptionMessageEnding;
                        // Если достигнут финальный статус, то его изменить нельзя.
                        if (project.getStatus().ordinal() == ProjectStatus.values().length - 1) {
                            exceptionMessageEnding = " cannot be changed, because it is the final status";
                        } else {
                            // Если статус не финальный, указываем на какой можнон его заменить
                            exceptionMessageEnding = " can only be changed to " +
                                                     ProjectStatus.values()[project.getStatus().ordinal() + 1];
                        }

                        throw new StatusModificationException("Current status " + project.getStatus() + exceptionMessageEnding);
                    }

                    project.setStatus(request.getStatus());
                    return project;
                })
                .map(repository::save)
                .map(projectMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));
    }
}
