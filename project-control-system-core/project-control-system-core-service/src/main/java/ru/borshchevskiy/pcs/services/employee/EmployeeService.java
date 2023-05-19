package ru.borshchevskiy.pcs.services.employee;

import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;

import java.util.List;

public interface EmployeeService {

    EmployeeDto getById(Long id);

    List<EmployeeDto> getAll();

    List<EmployeeDto> getByFilter(EmployeeFilter filter);

    EmployeeDto save(EmployeeDto dto);

    boolean deleteById(Long id);
}
