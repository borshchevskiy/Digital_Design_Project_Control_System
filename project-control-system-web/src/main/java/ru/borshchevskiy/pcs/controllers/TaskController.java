package ru.borshchevskiy.pcs.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;
import ru.borshchevskiy.pcs.services.task.TaskService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Задачи", description = "Управление задачами")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Получение задачи", description = "Получение задачи по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(taskService.findById(id));
    }

    @Operation(summary = "Получение задач", description = "Получение всех задач")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskDto>> getAll() {

        return ResponseEntity.status(OK).body(taskService.findAll());
    }

    @PostMapping(value="/filter", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<TaskDto> getAllByFilter(@RequestBody TaskFilter filter) {
        return taskService.findAllByFilter(filter);
    }

    @Operation(summary = "Создание задачи", description = "Создание новой задачи")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto request) {

        return ResponseEntity.status(OK).body(taskService.save(request));
    }

    @Operation(summary = "Изменение задачи", description = "Изменение задачи по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDto> updateTask(@RequestBody TaskDto request) {

        return ResponseEntity.status(OK).body(taskService.save(request));
    }

    @Operation(summary = "Удаление задачи", description = "Удаление задачи по id")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDto> deleteTask(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(taskService.deleteById(id));
    }

    @Operation(summary = "Изменение статуса", description = "Изменение статуса задачи по id")
    @PostMapping(value = "/{id}/status", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDto> updateStatus(@PathVariable Long id,
                                                @RequestBody TaskStatusDto request) {

        return ResponseEntity.status(OK).body(taskService.updateStatus(id, request));
    }

}
