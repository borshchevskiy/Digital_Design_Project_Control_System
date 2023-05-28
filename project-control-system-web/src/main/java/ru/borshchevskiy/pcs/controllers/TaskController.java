package ru.borshchevskiy.pcs.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;
import ru.borshchevskiy.pcs.services.task.TaskService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Задачи", description = "Управление задачами")
@SecurityRequirement(name = "Swagger auth")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Получение задачи", description = "Получение задачи по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public TaskDto getTask(@PathVariable Long id) {

        return taskService.findById(id);
    }

    @Operation(summary = "Получение задач", description = "Получение всех задач")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<TaskDto> getAll() {

        return taskService.findAll();
    }

    @PostMapping(value = "/filter", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<TaskDto> getAllByFilter(@RequestBody TaskFilter filter) {

        return taskService.findAllByFilter(filter);
    }

    @Operation(summary = "Создание задачи", description = "Создание новой задачи")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public TaskDto createTask(@RequestBody TaskDto request) {

        return taskService.save(request);
    }

    @Operation(summary = "Изменение задачи", description = "Изменение задачи по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public TaskDto updateTask(@RequestBody TaskDto request) {

        return taskService.save(request);
    }

    @Operation(summary = "Удаление задачи", description = "Удаление задачи по id")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public TaskDto deleteTask(@PathVariable Long id) {

        return taskService.deleteById(id);
    }

    @Operation(summary = "Изменение статуса", description = "Изменение статуса задачи по id")
    @PostMapping(value = "/{id}/status", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public TaskDto updateStatus(@PathVariable Long id,
                                @RequestBody TaskStatusDto request) {

        return taskService.updateStatus(id, request);
    }

}
