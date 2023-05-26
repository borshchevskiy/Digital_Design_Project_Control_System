package ru.borshchevskiy.pcs.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.services.employee.EmployeeService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/api/v1/employees")
@Tag(name = "Сотрудники", description = "Управление сотрудниками")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Получение сотрудника", description = "Получение сотруника по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(employeeService.findById(id));
    }

    @Operation(summary = "Получение сотрудников", description = "Получение всех сотруников")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EmployeeDto>> getAll() {

        return ResponseEntity.status(OK).body(employeeService.findAll());
    }

    @Operation(summary = "Поиск сотрудника по учетной записи", description = "Найти сотруника по точному соответствию учетной записи")
    @GetMapping(value = "/{account}", consumes = TEXT_PLAIN_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDto> getByAccount(@PathVariable String account) {

        return ResponseEntity.status(OK).body(employeeService.findByAccount(account));
    }

    @Operation(summary = "Поиск сотрудника по фильтру", description = "Найти сотруника по текстовому значению по полям " +
                                                                      "Фамилия, Имя, Отчество, учетной записи, " +
                                                                      "адресу электронной почты " +
                                                                      "и только среди активных сотрудников.")
    @PostMapping(value = "/filter", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EmployeeDto>> getAllByFilter(@RequestBody EmployeeFilter filter) {

        return ResponseEntity.status(OK).body(employeeService.findAllByFilter(filter));
    }


    @Operation(summary = "Изменение сотрудника", description = "Изменение сотруника по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto request) {

        return ResponseEntity.status(OK).body(employeeService.save(request));
    }


    @Operation(summary = "Удаление сотрудника", description = "Изменение по id статуса сотруника на DELETED")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDto> deleteEmployee(@PathVariable Long id) {

        return ResponseEntity.status(OK).body(employeeService.deleteById(id));

    }
}
