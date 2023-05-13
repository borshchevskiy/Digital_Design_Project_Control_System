package ru.borshchevskiy.pcs.services.employee;

import ru.borshchevskiy.pcs.dto.employee.request.EmployeeCreateDto;
import ru.borshchevskiy.pcs.dto.employee.request.EmployeeUpdateDto;
import ru.borshchevskiy.pcs.dto.employee.response.EmployeeReadDto;

import java.util.List;

public interface EmployeeService {

    EmployeeReadDto getById(Long id);

    List<EmployeeReadDto> getAll();

    EmployeeReadDto create(EmployeeCreateDto id);

    EmployeeReadDto update(EmployeeUpdateDto id);

    boolean deleteById(Long id);
}
