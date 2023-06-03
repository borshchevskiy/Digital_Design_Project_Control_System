package ru.borshchevskiy.pcs.service.mappers.task;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.teammember.TeamMemberRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

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
        task.setDateCreated(LocalDateTime.now());

        return task;
    }

    public Task mergeTask(Task task, TaskDto dto) {

        if (dto.getStatus() != task.getStatus()) {
            throw new RequestDataValidationException("Task's status can't be updated! Use specific request to update status");
        }

        if (dto.getDateCreated() != null && !task.getDateCreated().isEqual(dto.getDateCreated())) {
            throw new RequestDataValidationException("Task's status can't be updated! Use specific request to update status");
        }

        copyToTask(task, dto);

        task.setDateUpdated(LocalDateTime.now());

        return task;
    }

    private void copyToTask(Task copyTo, TaskDto copyFrom) {

        String principalName = SecurityContextHolder.getContext().getAuthentication().getName();

        Employee author = employeeRepository.findByUsername(principalName)
                .orElseThrow(() -> new RequestDataValidationException("Author must be an Employee!"));

        Project project = projectRepository.findById(copyFrom.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project with id=" + copyFrom.getProjectId() + " not found!"));

        List<Employee> projectEmployees = teamMemberRepository.findAllByProjectId(copyFrom.getProjectId()).stream()
                .map(TeamMember::getEmployee)
                .toList();

        if (!projectEmployees.contains(author)) {
            throw new RequestDataValidationException("Author must be participant of the project!");
        }

        Employee implementer = null;

        if (copyFrom.getImplementerId() != null) {
            implementer = employeeRepository.findById(copyFrom.getImplementerId())
                    .orElseThrow(() -> new NotFoundException("Employee with id=" + copyFrom.getImplementerId() + " not found!"));

            if (!projectEmployees.contains(implementer)) {
                throw new RequestDataValidationException("Implementer must be participant of the project!");
            }

            if (implementer.getStatus().equals(EmployeeStatus.DELETED)) {
                throw new RequestDataValidationException("Implementer must have status: ACTIVE.");
            }
        }

        copyTo.setName(copyFrom.getName());
        copyTo.setDescription(copyFrom.getDescription());
        copyTo.setImplementer(implementer);
        copyTo.setLaborCosts(Integer.valueOf(copyFrom.getLaborCosts()));
        copyTo.setDeadline(copyFrom.getDeadline());
        copyTo.setStatus(copyFrom.getStatus());
        copyTo.setAuthor(author);
        copyTo.setDateCreated(copyFrom.getDateCreated());
        copyTo.setDateUpdated(copyFrom.getDateUpdated());
        copyTo.setProject(project);

    }
}
