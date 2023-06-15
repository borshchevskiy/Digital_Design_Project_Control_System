package ru.borshchevskiy.pcs.repository.task;

import ru.borshchevskiy.pcs.entities.task.Task;

import java.util.List;
import java.util.Optional;

public interface TaskJdbcRepository {
    Task create(Task task);

    Task update(Task task);

    Optional<Task> getById(long id);

    List<Task> getAll();

    void deleteById(Long id);
}
