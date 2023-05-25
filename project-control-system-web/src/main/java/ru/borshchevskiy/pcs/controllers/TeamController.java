package ru.borshchevskiy.pcs.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.services.team.TeamService;
import ru.borshchevskiy.pcs.services.teammember.TeamMemberService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams")
@Tag(name = "Команды", description = "Управление командами")
public class TeamController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;

    @Operation(summary = "Получение команды", description = "Получение команды по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDto> getTeam(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(teamService.findById(id));
    }

    @Operation(summary = "Получение команд", description = "Получение всех команд")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamDto>> getAll() {

        return ResponseEntity.status(OK).body(teamService.findAll());
    }

    @Operation(summary = "Получение участников", description = "Создание всех участников команды")
    @GetMapping(value = "/{id}/teammembers", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamMemberDto>> getAllMembers(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(teamMemberService.findAllByTeamId(id));
    }

    @Operation(summary = "Создание команды", description = "Создание новой команды")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamDto request) {

        return ResponseEntity.status(OK).body(teamService.save(request));
    }

    @Operation(summary = "Изменение команды", description = "Изменение команды по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDto> updateTeam(@RequestBody TeamDto request) {

        return ResponseEntity.status(OK).body(teamService.save(request));
    }

    @Operation(summary = "Удаление команды", description = "Удаление команды по id")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDto> deleteTeam(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(teamService.deleteById(id));
    }

}
