package ru.borshchevskiy.pcs.service.services.task;

import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;
import ru.borshchevskiy.pcs.dto.task.filter.TaskFilter;
import ru.borshchevskiy.pcs.dto.task.status.TaskDto;

import java.util.List;

public interface TaskService {

    TaskDto findById(Long id);

    List<TaskDto> findAll();

    List<TaskDto> findAllByFilter(TaskFilter filter);

    TaskDto save(TaskDto dto);

    TaskDto deleteById(Long id);

    TaskDto updateStatus(Long id, TaskStatusDto request);

    List<TaskDto> findAllByProjectId(Long id);

    List<TaskDto> addReferences(Long id, List<Long> referenceIds);

    List<TaskDto> removeReferences(Long id, List<Long> referenceIds);


    List<TaskDto> getReferences(Long id);
}
