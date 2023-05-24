package ru.borshchevskiy.pcs.services.project.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.dto.project.ProjectFilter;
import ru.borshchevskiy.pcs.dto.project.ProjectStatusDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.mappers.project.ProjectMapper;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectSpecificationUtil;
import ru.borshchevskiy.pcs.services.project.ProjectService;

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
                    project.setStatus(request.getStatus());
                    return project;
                })
                .map(repository::save)
                .map(projectMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));
    }
}
