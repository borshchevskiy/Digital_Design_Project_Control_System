package ru.borshchevskiy.pcs.services.task.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.mappers.task.TaskMapper;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;
import ru.borshchevskiy.pcs.services.task.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto findById(Long id) {
        return repository.findById(id)
                .map(taskMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));
    }

    @Override
    public List<TaskDto> findAll() {
        return repository.findAll()
                .stream()
                .map(taskMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findAllByFilter(TaskFilter filter) {
        return repository.findAllByFilter(filter)
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
        return taskMapper.mapToDto(task);
    }

    private TaskDto update(TaskDto dto) {
        Task task = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Task with id=" + dto.getId() + " not found!"));

        taskMapper.mergeTask(task, dto);
        return taskMapper.mapToDto(repository.save(task));
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return repository.findById(id).map(project -> {
                    repository.delete(project);
                    return true;
                })
                .orElse(false);
    }
}
