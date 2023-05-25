package ru.borshchevskiy.pcs.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.dto.project.ProjectFilter;
import ru.borshchevskiy.pcs.dto.project.ProjectStatusDto;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.services.project.ProjectService;
import ru.borshchevskiy.pcs.services.task.TaskService;
import ru.borshchevskiy.pcs.services.teammember.TeamMemberService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Проекты", description = "Управление проектами")
public class ProjectController {

    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final TaskService taskService;

    @Operation(summary = "Получение проекта", description = "Получение проекта по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(projectService.findById(id));
    }

    @Operation(summary = "Получение проектов", description = "Получение всех проектов")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProjectDto>> getAll() {

        return ResponseEntity.status(OK).body(projectService.findAll());
    }

    @PostMapping(value = "/filter", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<ProjectDto> getAllByFilter(@RequestBody ProjectFilter filter) {
        return projectService.findAllByFilter(filter);
    }

    @Operation(summary = "Получение участников проекта", description = "Получение всех участников проекта")
    @GetMapping(value = "/{id}/teammembers", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamMemberDto>> getAllMembers(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(teamMemberService.findAllByProjectId(id));
    }

    @Operation(summary = "Получение участников задач", description = "Получение всех задач по проекту")
    @GetMapping(value = "/{id}/tasks", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskDto>> getAllTasks(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(taskService.findAllByProjectId(id));
    }

    @Operation(summary = "Создание проекта", description = "Создание нового проекта")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto request) {

        return ResponseEntity.status(OK).body(projectService.save(request));
    }

    @Operation(summary = "Изменение проекта", description = "Изменение проекта по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDto> updateProject(@RequestBody ProjectDto request) {

        return ResponseEntity.status(OK).body(projectService.save(request));
    }

    @Operation(summary = "Удаление проекта", description = "Удаление проекта по id")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDto> deleteProject(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(projectService.deleteById(id));
    }

    @Operation(summary = "Изменение статуса", description = "Изменение статуса проекта по id")
    @PostMapping(value = "/{id}/status", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDto> updateStatus(@PathVariable Long id,
                                                   @RequestBody ProjectStatusDto request) {

        return ResponseEntity.status(OK).body(projectService.updateStatus(id, request));
    }

}
