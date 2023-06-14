package ru.borshchevskiy.pcs.repository.employee;

import ru.borshchevskiy.pcs.dto.employee.filter.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeFileRepository {

    Employee create(Employee employee);

    Employee update(Employee employee);

    Optional<Employee> findById(long id);

    List<Employee> findAll();

    /*
    Этот метод физически удаляет файл
     */
    void deleteById(Long id);

}
