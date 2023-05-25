package ru.borshchevskiy.pcs.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.services.employee.EmployeeService;

import java.util.List;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto createEmployee(@RequestBody EmployeeDto request) {
        return employeeService.save(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDto getEmployee(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDto> getAllByFilter(@RequestBody EmployeeFilter filter) {
        return employeeService.findAllByFilter(filter);
    }

    @GetMapping("/filter/account")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDto getEmployeeByAccount(@RequestBody EmployeeFilter filter) {
        return employeeService.findByAccount(filter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmployeeDto> delete(@PathVariable Long id) {

        return new ResponseEntity<>(employeeService.deleteById(id), HttpStatus.OK);
    }

}
