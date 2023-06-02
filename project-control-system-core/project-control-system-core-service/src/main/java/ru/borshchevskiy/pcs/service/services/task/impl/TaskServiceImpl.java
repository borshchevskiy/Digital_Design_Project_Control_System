package ru.borshchevskiy.pcs.service.services.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;
import ru.borshchevskiy.pcs.repository.task.TaskSpecificationUtil;
import ru.borshchevskiy.pcs.service.mappers.task.TaskMapper;
import ru.borshchevskiy.pcs.service.services.email.EmailService;
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper taskMapper;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public TaskDto findById(Long id) {
        return repository.findById(id)
                .map(taskMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findAll() {
        return repository.findAll()
                .stream()
                .map(taskMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findAllByFilter(TaskFilter filter) {
        return repository.findAll(TaskSpecificationUtil.getSpecification(filter))
                .stream()
                .map(taskMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findAllByProjectId(Long id) {
        return repository.findAllByProjectId(id)
                .stream()
                .map(taskMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDto save(TaskDto dto) {
        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private TaskDto create(TaskDto dto) {
        Task task = repository.save(taskMapper.createTask(dto));

        log.info("Task id=" + task.getId() + " created.");

        eventPublisher.publishEvent(task);

        return taskMapper.mapToDto(task);
    }

    private TaskDto update(TaskDto dto) {
        Task task = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Task with id=" + dto.getId() + " not found!"));

        taskMapper.mergeTask(task, dto);

        log.info("Task id=" + task.getId() + " updated.");

        return taskMapper.mapToDto(repository.save(task));
    }

    @Override
    @Transactional
    public TaskDto deleteById(Long id) {
        Task task = repository.findById(id)
                .map(t -> {
                    repository.delete(t);
                    return t;
                }).orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));

        log.info("Task id=" + task.getId() + " deleted.");

        return taskMapper.mapToDto(task);

    }

    @Override
    @Transactional
    public TaskDto updateStatus(Long id, TaskStatusDto request) {
        return repository.findById(id)
                .map(task -> {
                    task.setStatus(request.getStatus());
                    return task;
                })
                .map(repository::save)
                .map(taskMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));
    }


}
