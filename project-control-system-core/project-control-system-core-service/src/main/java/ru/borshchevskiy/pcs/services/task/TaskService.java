package ru.borshchevskiy.pcs.services.task;

import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;

import java.util.List;

public interface TaskService {

    TaskDto getById(Long id);

    List<TaskDto> getAll();

    List<TaskDto> getByFilter(TaskFilter filter);

    TaskDto save(TaskDto dto);

    boolean deleteById(Long id);
}
