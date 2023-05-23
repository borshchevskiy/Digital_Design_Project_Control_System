package ru.borshchevskiy.pcs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.task.TaskDto;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.services.team.TeamService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TeamDto getTask(@PathVariable Long id) {
        return teamService.findById(id);
    }
}
