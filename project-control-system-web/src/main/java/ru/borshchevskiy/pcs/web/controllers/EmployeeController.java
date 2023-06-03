package ru.borshchevskiy.pcs.web.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/api/v1/employees")
@Tag(name = "Сотрудники", description = "Управление сотрудниками")
@SecurityRequirement(name = "Swagger auth")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Получение сотрудника", description = "Получение сотруника по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public EmployeeDto getEmployee(@PathVariable Long id) {


        return employeeService.findById(id);
    }

    @Operation(summary = "Получение сотрудников", description = "Получение всех сотруников")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<EmployeeDto> getAll() {

        return employeeService.findAll();
    }

    @Operation(summary = "Поиск сотрудника по учетной записи", description = "Найти сотруника по точному соответствию учетной записи")
    @GetMapping(value = "/usernames/{username}", produces = APPLICATION_JSON_VALUE)
    public EmployeeDto getByUsername(@PathVariable String username) {

        return employeeService.findByUsername(username);
    }

    @Operation(summary = "Поиск сотрудника по фильтру", description = "Найти сотруника по текстовому значению по полям " +
                                                                      "Фамилия, Имя, Отчество, учетной записи, " +
                                                                      "адресу электронной почты " +
                                                                      "и только среди активных сотрудников.")
    @PostMapping(value = "/filter", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<EmployeeDto> getAllByFilter(@RequestBody EmployeeFilter filter) {

        return employeeService.findAllByFilter(filter);
    }

    @Operation(summary = "Создание сотрудника", description = "Создание нового сотрудника")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public EmployeeDto createEmployee(@RequestBody EmployeeDto request) {

        return employeeService.save(request);
    }


    @Operation(summary = "Изменение сотрудника", description = "Изменение сотруника по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public EmployeeDto updateEmployee(@RequestBody EmployeeDto request) {

        return employeeService.save(request);
    }


    @Operation(summary = "Удаление сотрудника", description = "Изменение по id статуса сотруника на DELETED")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public EmployeeDto deleteEmployee(@PathVariable Long id) {

        return employeeService.deleteById(id);

    }
}
