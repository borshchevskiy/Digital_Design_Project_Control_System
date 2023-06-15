package ru.borshchevskiy.pcs.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.service.services.teammember.TeamMemberService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teammembers")
@Tag(name = "Участники команды", description = "Управление участниками команды")
@SecurityRequirement(name = "Swagger auth")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @Operation(summary = "Получение участника", description = "Получение участника по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public TeamMemberDto getTeamMember(@PathVariable Long id) {

        return teamMemberService.findById(id);
    }

    @Operation(summary = "Получение участников", description = "Получение всех участников")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<TeamMemberDto> getAll() {

        return teamMemberService.findAll();
    }

    @Operation(summary = "Создание участника", description = "Создание нового участника")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public TeamMemberDto createTeamMember(@RequestBody TeamMemberDto request) {

        return teamMemberService.save(request);
    }

    @Operation(summary = "Изменение участника", description = "Изменение участника по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public TeamMemberDto updateTeamMember(@RequestBody TeamMemberDto request) {

        return teamMemberService.save(request);
    }

    @Operation(summary = "Удаление участника", description = "Удаление участника по id")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public TeamMemberDto deleteTeamMember(@PathVariable Long id) {

        return teamMemberService.deleteById(id);
    }
}
