package ru.borshchevskiy.pcs.services.employee;

import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    EmployeeDto getById(Long id);

    List<EmployeeDto> getAll();

    EmployeeDto save(EmployeeDto dto);

    boolean deleteById(Long id);
}
