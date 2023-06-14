package ru.borshchevskiy.pcs.repository.employee;

import ru.borshchevskiy.pcs.dto.employee.filter.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeJdbcRepository {

    Employee create(Employee employee);

    Employee update(Employee employee);

    Optional<Employee> findById(long id);

    List<Employee> findAll();

    void deleteById(Long id);

    /*
            Поиск сотрудников.
            Поиск осуществляется по текстовому значению, которое проверяется по атрибутам Фамилия, Имя, Отчество,
            учетной записи, адресу электронной почты и только среди активных сотрудников.
             */
    List<Employee> findByFilter(EmployeeFilter filter);
}
