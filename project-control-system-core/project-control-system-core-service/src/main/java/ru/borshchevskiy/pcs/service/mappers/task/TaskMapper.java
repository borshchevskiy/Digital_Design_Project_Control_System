package ru.borshchevskiy.pcs.service.mappers.task;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.task.status.TaskDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;

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
        taskDto.setImplementerId(task.getImplementer() == null
                ? null
                : task.getImplementer().getId());
        taskDto.setLaborCosts(task.getLaborCosts());
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

        // Задача должна создаваться в статусе NEW
        task.setStatus(TaskStatus.NEW);

        return task;
    }

    public Task mergeTask(Task task, TaskDto dto) {

        copyToTask(task, dto);

        return task;
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));
    }

    private Project getEProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project with id=" + id + " not found!"));
    }

    private void copyToTask(Task copyTo, TaskDto copyFrom) {

        // Автором автоматически становится текущий авторизованный пользователь
        String principalName = SecurityContextHolder.getContext().getAuthentication().getName();

        Employee author = employeeRepository.findByUsername(principalName)
                .orElseThrow(() -> new RequestDataValidationException("Author must be an Employee!"));

        copyTo.setName(copyFrom.getName());
        copyTo.setDescription(copyFrom.getDescription());
        // Т.к. исполнитель не обязательное поле, в случае его отсутствия оставляем null
        copyTo.setImplementer(copyFrom.getImplementerId() == null
                ? null
                : getEmployee(copyFrom.getImplementerId()));
        copyTo.setLaborCosts(copyFrom.getLaborCosts());
        copyTo.setDeadline(copyFrom.getDeadline());
        copyTo.setStatus(copyFrom.getStatus());
        copyTo.setAuthor(author);
        copyTo.setDateCreated(copyFrom.getDateCreated());
        copyTo.setDateUpdated(copyFrom.getDateUpdated());
        copyTo.setProject(getEProject(copyFrom.getProjectId()));
    }
}
