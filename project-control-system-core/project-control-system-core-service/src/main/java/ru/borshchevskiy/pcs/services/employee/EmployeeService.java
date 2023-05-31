package ru.borshchevskiy.pcs.services.employee;

import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;

import java.util.List;

public interface EmployeeService {

    EmployeeDto findById(Long id);

    List<EmployeeDto> findAll();

    List<EmployeeDto> findAllByFilter(EmployeeFilter filter);

    EmployeeDto findByUsername(String account);

    EmployeeDto save(EmployeeDto dto);

    EmployeeDto deleteById(Long id);
}
