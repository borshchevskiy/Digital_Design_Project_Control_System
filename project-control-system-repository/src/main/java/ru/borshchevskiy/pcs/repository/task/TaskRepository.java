package ru.borshchevskiy.pcs.repository.task;

import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.task.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task create(Task task);

    Task update(Task task);

    Optional<Task> getById(long id);

    List<Task> getAll();

    void deleteById(Long id);

    List<Task> findByFilter(TaskFilter filter);
}
