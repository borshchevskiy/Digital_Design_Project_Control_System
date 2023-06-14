package ru.borshchevskiy.pcs.service.services.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.common.exceptions.StatusModificationException;
import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;
import ru.borshchevskiy.pcs.dto.task.filter.TaskFilter;
import ru.borshchevskiy.pcs.dto.task.status.TaskDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.teammember.TeamMember;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.project.ProjectRepository;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;
import ru.borshchevskiy.pcs.repository.task.TaskSpecificationUtil;
import ru.borshchevskiy.pcs.repository.teammember.TeamMemberRepository;
import ru.borshchevskiy.pcs.service.mappers.task.TaskMapper;
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TaskMapper taskMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(readOnly = true)
    public TaskDto findById(Long id) {
        return repository.findById(id)
                .map(taskMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findAll() {
        return repository.findAll()
                .stream()
                .map(taskMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findAllByFilter(TaskFilter filter) {
        return repository.findAll(TaskSpecificationUtil.getSpecification(filter))
                .stream()
                .map(taskMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findAllByProjectId(Long id) {
        return repository.findAllByProjectId(id)
                .stream()
                .map(taskMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDto save(TaskDto dto) {

        // Проверяем что обязательные поля присутствуют
        if (!StringUtils.hasText(dto.getName())
            || dto.getLaborCosts() == null
            || dto.getDeadline() == null) {
            throw new RequestDataValidationException("Name, labor costs and deadline must be specified");
        }

        // Проверяем что проект, в рамках которого создается задача, существует
        if (projectRepository.findById(dto.getProjectId()).isEmpty()) {
            throw new NotFoundException("Project with id=" + dto.getProjectId() + " not found!");
        }

        // Проверяем что автор является сотрудником и участником проекта
        String principalName = SecurityContextHolder.getContext().getAuthentication().getName();

        Employee author = employeeRepository.findByUsername(principalName)
                .orElseThrow(() -> new RequestDataValidationException("Author must be an Employee!"));

        List<Employee> projectEmployees = teamMemberRepository.findAllByProjectId(dto.getProjectId()).stream()
                .map(TeamMember::getEmployee)
                .toList();

        if (!projectEmployees.contains(author)) {
            throw new RequestDataValidationException("Author must be participant of the project!");
        }

        // Проверяем что исполнитель существует, является участником проекта и не удален
        if (dto.getImplementerId() != null) {
            Employee implementer = employeeRepository.findById(dto.getImplementerId())
                    .orElseThrow(() -> new NotFoundException("Employee with id=" + dto.getImplementerId() + " not found!"));

            if (!projectEmployees.contains(implementer)) {
                throw new RequestDataValidationException("Implementer must be participant of the project!");
            }

            if (implementer.getStatus().equals(EmployeeStatus.DELETED)) {
                throw new RequestDataValidationException("Implementer must have status: ACTIVE.");
            }
        }

        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private TaskDto create(TaskDto dto) {

        // Фиксируем дату создания и проверяем крайний срок
        LocalDateTime dateCreated = LocalDateTime.now();
        LocalDateTime minimumDeadline = calculateMinimumDeadline(dto.getLaborCosts(), dateCreated);

        // Проверяем что крайний срок не ранее чем дата создания + трудозатраты
        if (dto.getDeadline().isBefore(minimumDeadline)) {
            throw new RequestDataValidationException("Deadline is too early!");
        }

        // Создаем задачу с датой, для которой выполнена валидация
        Task task = repository.save(taskMapper.createTask(dto));

        log.debug("Task id=" + task.getId() + " created.");

        rabbitTemplate.convertAndSend("app.task", "app.task.new", task);

        return taskMapper.mapToDto(task);
    }

    private TaskDto update(TaskDto dto) {
        Task task = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Task with id=" + dto.getId() + " not found!"));

        // Проверяем что не меняется статус, для его изменения существует отдельный метод
        if (dto.getStatus() == null || dto.getStatus() != task.getStatus()) {
            throw new RequestDataValidationException("Task's status can't be updated! " +
                                                     "Use specific request to update status");
        }

        // Проверяем что не меняется дата создания
        if (dto.getDateCreated() == null || !dto.getDateCreated().isEqual(task.getDateCreated())) {
            throw new RequestDataValidationException("Task's creation date can't be changed.");
        }

        // Проверяем что при увеличении трудозатрат и/или при сдвиге дэдлайна влево сохраняется
        // соответствие: дата дэдэлайна >= дата создания + трудозатраты.
        if (dto.getLaborCosts() > task.getLaborCosts() || dto.getDeadline().isBefore(task.getDeadline())) {
            LocalDateTime minimumDeadline = calculateMinimumDeadline(dto.getLaborCosts(), task.getDateCreated());
            if (dto.getDeadline().isBefore(minimumDeadline)) {
                throw new RequestDataValidationException("Deadline is too early!");
            }
        }

        task = taskMapper.mergeTask(task, dto);

        log.debug("Task id=" + task.getId() + " updated.");

        return taskMapper.mapToDto(repository.save(task));
    }

    // Метод вычисляет наиболее ранний крайний срок на основании количества часов трудозатрат
    private LocalDateTime calculateMinimumDeadline(Integer laborHours, LocalDateTime dateCreated) {
        // Считаем количество рабочих дней на основе количества часов трудозатрат
        // Если часы не делятся нацело на 8, добавляем 1 день
        int days = laborHours / 8;
        if (laborHours % 8 != 0) {
            days++;
        }

        // Рассчитываем минимальный крайний срок, с учетом выходных дней (суббот и воскресений)
        LocalDateTime calculatedDeadline = LocalDateTime.from(dateCreated);
        while (days > 0) {
            DayOfWeek dayOfWeek = calculatedDeadline.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY
                && dayOfWeek != DayOfWeek.SUNDAY) {
                days--;
            }
            calculatedDeadline = calculatedDeadline.plus(1, ChronoUnit.DAYS);
        }
        return calculatedDeadline;
    }

    @Override
    @Transactional
    public TaskDto deleteById(Long id) {
        Task task = repository.findById(id)
                .map(t -> {
                    repository.delete(t);
                    return t;
                }).orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));

        log.debug("Task id=" + task.getId() + " deleted.");

        return taskMapper.mapToDto(task);

    }

    @Override
    @Transactional
    public TaskDto updateStatus(Long id, TaskStatusDto request) {
        if (request.getStatus() == null) {
            throw new StatusModificationException("Status can't be null.");
        }

        return repository.findById(id)
                .map(task -> {
                    TaskStatus currentStatus = task.getStatus();
                    TaskStatus newStatus = request.getStatus();

                    // Если уже установлен финальный статус, то его изменить нельзя.
                    if (task.getStatus().ordinal() == TaskStatus.values().length - 1) {
                        throw new StatusModificationException("Current status " + task.getStatus() +
                                                              " cannot be changed, because it is the final status");
                    }

                    // Изменение статуса возможно только на следующий статус по цепочке,
                    // нельзя выставить предыдущий статус или перескочить через один.
                    // Если в запросе неверный статус, указываем на какой можно его заменить
                    if (newStatus.ordinal() - currentStatus.ordinal() != 1) {
                        throw new StatusModificationException("Current status " + task.getStatus() +
                                                              " can only be changed to " +
                                                              TaskStatus.values()[task.getStatus().ordinal() + 1]);
                    }
                    // Задачу нельзя перевести в статус CLOSED если она связана с другими задачами и они еще не в статусе CLOSED
                    if (newStatus.equals(TaskStatus.CLOSED)) {
                        task.getReferencesTo().stream()
                                .filter(t -> !t.getStatus().equals(TaskStatus.CLOSED))
                                .findFirst()
                                .ifPresent((t) ->
                                {
                                    throw new StatusModificationException("Task can't be closed because reference " +
                                                                          "tasks are still not closed");
                                });
                    }

                    task.setStatus(request.getStatus());
                    return task;
                })
                .map(repository::save)
                .map(taskMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));
    }

    // Метод добавляет не зависимости к задаче. Метод возвращает список всех зависимостей задачи
    @Override
    @Transactional
    public List<TaskDto> addReferences(Long id, List<Long> referenceIds) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));

        if (referenceIds.isEmpty()) {
            throw new RequestDataValidationException("No references specified");
        }

        // Собираем список task'ов по айдишникам из запроса
        List<Task> references = new ArrayList<>();
        for (long refId : referenceIds) {
            Task refTask = repository.findById(refId)
                    .orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));
            // Проверяем что task, на который мы хотим ссылаться уже не ссылается на наш task, чтобы избежать рекурсии
            if (task.getReferencedBy().contains(refTask)) {
                throw new RequestDataValidationException("Can't set reference to task id=" + refTask.getId() +
                                                         " because it references initial task.");
            }
            references.add(refTask);
        }

        references.stream()
                .filter(t -> !task.getReferencesTo().contains(t)) // Выбираем только те на которые task еще не ссылается
                .forEach(task::addReference);

        repository.save(task);

        return getReferences(id);
    }

    // Метод удаляет зависимости из задачи. Метод возвращает список всех зависимостей задачи
    @Override
    @Transactional
    public List<TaskDto> removeReferences(Long id, List<Long> referenceIds) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));

        if (referenceIds.isEmpty()) {
            throw new RequestDataValidationException("No references found");
        }

        List<Task> references = new ArrayList<>();

        for (long refId : referenceIds) {
            Task refTask = repository.findById(refId)
                    .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));
            references.add(refTask);
        }

        references.forEach(task::removeReference);
        repository.save(task);

        return getReferences(id);
    }

    // Метод возвращает список всех зависимостей задачи
    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getReferences(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));

        return task.getReferencesTo().stream().map(taskMapper::mapToDto).collect(Collectors.toList());
    }


}
