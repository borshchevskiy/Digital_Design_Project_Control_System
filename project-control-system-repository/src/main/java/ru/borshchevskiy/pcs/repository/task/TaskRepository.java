package ru.borshchevskiy.pcs.repository.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.borshchevskiy.pcs.entities.task.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {


    List<Task> findAllByProjectId(Long id);
}
