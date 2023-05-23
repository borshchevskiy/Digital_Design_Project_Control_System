package ru.borshchevskiy.pcs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.services.task.TaskService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto getTask(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDto> getAllByFilter(@RequestBody TaskFilter filter) {
        return taskService.findAllByFilter(filter);
    }
}
