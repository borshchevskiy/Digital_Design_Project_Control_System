package ru.borshchevskiy.pcs.service.mappers.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.enums.TaskStatus;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    public TaskDto mapToDto(Task task) {
        TaskDto taskDto = new TaskDto();

        taskDto.setId(task.getId());
        taskDto.setName(task.getName());
        taskDto.setDescription(task.getDescription());
        taskDto.setImplementerId(task.getImplementer().getId());
        taskDto.setLaborCosts(task.getLaborCosts().toString());
        taskDto.setDeadline(task.getDeadline());
        taskDto.setStatus(task.getStatus());
        taskDto.setAuthorId(task.getAuthor().getId());
        taskDto.setDateCreated(task.getDateCreated());
        taskDto.setDateUpdated(task.getDateUpdated());
        taskDto.setProjectId(task.getProject().getId());

        return taskDto;
    }

    public Task createTask(TaskDto dto) {
        Task task = new Task();

        copyToTask(task, dto);

        task.setStatus(TaskStatus.NEW);

        return task;
    }

    public void mergeTask(Task task, TaskDto dto) {

        copyToTask(task, dto);
    }

    private Employee getEmployee(Long id) {
        return Optional.ofNullable(id)
                .flatMap(employeeRepository::findById)
                .orElse(null);
    }

    private Project getProject(Long id) {
        return Optional.ofNullable(id)
                .flatMap(projectRepository::findById)
                .orElse(null);
    }

    private void copyToTask(Task copyTo, TaskDto copyFrom) {
        copyTo.setName(copyFrom.getName());
        copyTo.setDescription(copyFrom.getDescription());
        copyTo.setImplementer(getEmployee(copyFrom.getImplementerId()));
        copyTo.setLaborCosts(Integer.valueOf(copyFrom.getLaborCosts()));
        copyTo.setDeadline(copyFrom.getDeadline());
        copyTo.setStatus(copyFrom.getStatus());
        copyTo.setAuthor(getEmployee(copyFrom.getAuthorId()));
        copyTo.setDateCreated(copyFrom.getDateCreated());
        copyTo.setDateUpdated(copyFrom.getDateUpdated());
        copyTo.setProject(getProject(copyFrom.getProjectId()));


    }
}
