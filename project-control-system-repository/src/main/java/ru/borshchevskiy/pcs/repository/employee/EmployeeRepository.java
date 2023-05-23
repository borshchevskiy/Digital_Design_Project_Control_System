package ru.borshchevskiy.pcs.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.borshchevskiy.pcs.entities.employee.Employee;

import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeFilterRepository {

    Optional<Employee> findByAccount(String account);

}
