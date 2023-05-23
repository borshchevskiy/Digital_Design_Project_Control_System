package ru.borshchevskiy.pcs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.services.project.ProjectService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto getProject(@PathVariable Long id) {
        return projectService.findById(id);
    }


}
