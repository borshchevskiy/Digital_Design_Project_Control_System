package ru.borshchevskiy.pcs.controllers;

import ru.borshchevskiy.pcs.dto.employee.request.EmployeeCreateDto;
import ru.borshchevskiy.pcs.dto.employee.request.EmployeeUpdateDto;
import ru.borshchevskiy.pcs.dto.employee.response.EmployeeReadDto;
import ru.borshchevskiy.pcs.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.services.employee.impl.EmployeeServiceImpl;

import java.util.List;

public class EmployeeController {

    private final EmployeeService employeeService = new EmployeeServiceImpl();

    public EmployeeReadDto create(EmployeeCreateDto request) {
        return employeeService.create(request);
    }

    public EmployeeReadDto update(EmployeeUpdateDto request) {
        return employeeService.update(request);
    }

    public EmployeeReadDto getById(Long id) {
        return employeeService.getById(id);
    }

    public List<EmployeeReadDto> getAll() {
        return employeeService.getAll();
    }

    public boolean deleteById(Long id) {
        return employeeService.deleteById(id);
    }
}
