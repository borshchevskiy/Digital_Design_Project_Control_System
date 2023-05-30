package ru.borshchevskiy.pcs.service.services.task;

import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;

import java.util.List;

public interface TaskService {

    TaskDto findById(Long id);

    List<TaskDto> findAll();

    List<TaskDto> findAllByFilter(TaskFilter filter);

    TaskDto save(TaskDto dto);

    TaskDto deleteById(Long id);

    TaskDto updateStatus(Long id, TaskStatusDto request);

    List<TaskDto> findAllByProjectId(Long id);

}
