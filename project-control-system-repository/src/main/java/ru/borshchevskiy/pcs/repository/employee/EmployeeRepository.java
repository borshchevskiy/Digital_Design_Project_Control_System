package ru.borshchevskiy.pcs.repository.employee;

import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

    Employee create(Employee employee);

    Employee update(Employee employee);

    Optional<Employee> getById(long id);

    List<Employee> getAll();

    void deleteById(Long id);

    List<Employee> findByFilter(EmployeeFilter filter);
}
