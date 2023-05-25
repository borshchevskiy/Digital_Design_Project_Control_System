package ru.borshchevskiy.pcs.repository.task;

import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.entities.task.Task;

import java.util.List;

public interface TaskFilterRepository {

    List<Task> findAllByFilter(TaskFilter filter);
}
