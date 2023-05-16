package ru.borshchevskiy.pcs.controllers;


import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;

import ru.borshchevskiy.pcs.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.services.employee.impl.EmployeeServiceImpl;

import java.util.List;

public class EmployeeController {

    private final EmployeeService employeeService = new EmployeeServiceImpl();

    public EmployeeDto create(EmployeeDto request) {
        return employeeService.save(request);
    }

    public EmployeeDto update(EmployeeDto request) {
        return employeeService.save(request);
    }

    public EmployeeDto getById(Long id) {
        return employeeService.getById(id);
    }

    public List<EmployeeDto> getAll() {
        return employeeService.getAll();
    }

    public boolean deleteById(Long id) {
        return employeeService.deleteById(id);
    }
}
