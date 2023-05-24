package ru.borshchevskiy.pcs.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.services.teammember.TeamMemberService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teammembers")
@Tag(name = "Участники команды", description = "Управление участниками команды")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @Operation(summary = "Получение участника", description = "Получение участника по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamMemberDto> getTeam(@PathVariable Long id) {
        return ResponseEntity.status(OK).body(teamMemberService.findById(id));
    }

    @Operation(summary = "Получение участников", description = "Получение всех участников")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamMemberDto>> getAll() {
        return ResponseEntity.status(OK).body(teamMemberService.findAll());
    }

    @Operation(summary = "Создание участика", description = "Создание нового участника")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamMemberDto> createTeam(@RequestBody TeamMemberDto request) {
        return ResponseEntity.status(OK).body(teamMemberService.save(request));
    }

    @Operation(summary = "Изменение участника", description = "Изменение участника по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamMemberDto> updateTask(@RequestBody TeamMemberDto request) {
        return ResponseEntity.status(OK).body(teamMemberService.save(request));
    }

    @Operation(summary = "Удаление участника", description = "Удаление участника по id")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamMemberDto> deleteTask(@PathVariable Long id) {
        return ResponseEntity.status(OK).body(teamMemberService.deleteById(id));
    }
}
