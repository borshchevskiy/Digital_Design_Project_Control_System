package ru.borshchevskiy.pcs.service.services.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.StatusModificationException;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;
import ru.borshchevskiy.pcs.repository.task.TaskSpecificationUtil;
import ru.borshchevskiy.pcs.service.mappers.task.TaskMapper;
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper taskMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final RabbitTemplate rabbitTemplate;

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

        log.debug("Task id=" + task.getId() + " created.");

//        TODO: После согласования оставить одну публикацию
//        eventPublisher.publishEvent(task);
        rabbitTemplate.convertAndSend("app.task", "app.task.new", task);

        return taskMapper.mapToDto(task);
    }

    private TaskDto update(TaskDto dto) {
        Task task = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Task with id=" + dto.getId() + " not found!"));

        // TODO проверка изменения статуса

        task = taskMapper.mergeTask(task, dto);

        log.debug("Task id=" + task.getId() + " updated.");

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

        log.debug("Task id=" + task.getId() + " deleted.");

        return taskMapper.mapToDto(task);

    }

    @Override
    @Transactional
    public TaskDto updateStatus(Long id, TaskStatusDto request) {
        return repository.findById(id)
                .map(task -> {
                    TaskStatus currentStatus = task.getStatus();
                    TaskStatus newStatus = request.getStatus();

                    // Если достигнут финальный статус, то его изменить нельзя.
                    if (task.getStatus().ordinal() == TaskStatus.values().length - 1) {
                        throw new StatusModificationException("Current status " + task.getStatus() +
                                                              " cannot be changed, because it is the final status");
                    }

                    // Изменение статуса возможно только на следующий статус по цепочке,
                    // нельзя выставить предыдущий статус или перескочить через один.
                    // Если в запросе неверный статус, указываем на какой можно его заменить
                    if (newStatus.ordinal() - currentStatus.ordinal() != 1) {
                        throw new StatusModificationException("Current status " + task.getStatus() +
                                                              " can only be changed to " +
                                                              TaskStatus.values()[task.getStatus().ordinal() + 1]);
                    }

                    task.setStatus(request.getStatus());
                    return task;
                })
                .map(repository::save)
                .map(taskMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));
    }
}
