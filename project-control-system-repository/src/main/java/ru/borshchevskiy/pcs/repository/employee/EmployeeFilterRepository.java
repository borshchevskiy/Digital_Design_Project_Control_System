package ru.borshchevskiy.pcs.repository.employee;

import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;

import java.util.List;

public interface EmployeeFilterRepository {

    List<Employee> findAllByFilter(EmployeeFilter filter);
}
