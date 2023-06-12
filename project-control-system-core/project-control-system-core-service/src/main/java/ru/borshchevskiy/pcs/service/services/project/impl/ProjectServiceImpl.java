package ru.borshchevskiy.pcs.service.services.project.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.borshchevskiy.pcs.common.enums.ProjectStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.common.exceptions.StatusModificationException;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.dto.project.ProjectFilter;
import ru.borshchevskiy.pcs.dto.project.ProjectStatusDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectSpecificationUtil;
import ru.borshchevskiy.pcs.service.mappers.project.ProjectMapper;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

        // Т.к. код и наименование являются обязательными, сразу проверяем их наличие
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new RequestDataValidationException("Project's code and name can't be empty");
        }

        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private ProjectDto create(ProjectDto dto) {

        // Проверяем уникальность Project Code
        if (repository.findByCode(dto.getCode()).isPresent()) {
            throw new RequestDataValidationException("Project with code " + dto.getCode() + " already exists!");
        }

        // Создаем новый Project
        Project project = repository.save(projectMapper.createProject(dto));
        log.debug("Project id=" + project.getId() + " created.");
        return projectMapper.mapToDto(project);
    }

    private ProjectDto update(ProjectDto dto) {

        Project project = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Project with id=" + dto.getId() + " not found!"));

        // Статус обязателен, он не может быть null или изменен при изменени проекта
        if (dto.getStatus() != project.getStatus()) {
            throw new RequestDataValidationException("Project status can't be changed! " +
                                                     "Use specific method to change status.");
        }

        // При изменении кода, проверяем не занят ли уже этот код. Коды должны быть уникальны.
        if (!dto.getCode().equals(project.getCode())) {

            // Ищем Project по code, если нашелся, бросаем исключение
            if (repository.findByCode(dto.getCode()).isPresent()) {
                throw new RequestDataValidationException("Project with code " + dto.getCode() + " already exists!");
            }
        }

        project = projectMapper.mergeProject(project, dto);

        log.debug("Project id=" + project.getId() + " updated.");

        return projectMapper.mapToDto(repository.save(project));
    }

    @Override
    @Transactional
    public ProjectDto deleteById(Long id) {

        Project project = repository.findById(id)
                .map(p -> {
                    repository.delete(p);
                    return p;
                }).orElseThrow(() -> new NotFoundException("Project with id=" + id + " not found!"));

        log.debug("Project id=" + project.getId() + " deleted.");
        return projectMapper.mapToDto(project);

    }

    @Override
    @Transactional
    public ProjectDto updateStatus(Long id, ProjectStatusDto request) {
        if (request.getStatus() == null) {
            throw new StatusModificationException("Status can't be null.");
        }

        return repository.findById(id)
                .map(project -> {
                    ProjectStatus currentStatus = project.getStatus();
                    ProjectStatus newStatus = request.getStatus();

                    // Если достигнут финальный статус, то его изменить нельзя.
                    if (project.getStatus().ordinal() == ProjectStatus.values().length - 1) {
                        throw new StatusModificationException("Current status " + project.getStatus() +
                                                              " cannot be changed, because it is the final status");
                    }

                    // Изменение статуса возможно только на следующий статус по цепочке,
                    // нельзя выставить предыдущий статус или перескочить через один.
                    // Если в запросе неверный статус, указываем на какой можно его изменить
                    if (newStatus.ordinal() - currentStatus.ordinal() != 1) {
                        throw new StatusModificationException("Current status " + project.getStatus() +
                                                              " can only be changed to " +
                                                              ProjectStatus.values()[project.getStatus().ordinal() + 1]);
                    }

                    project.setStatus(request.getStatus());
                    return project;
                })
                .map(repository::save)
                .map(projectMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Project with id=" + id + " not found!"));
    }
}
