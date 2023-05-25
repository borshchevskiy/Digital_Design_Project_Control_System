package ru.borshchevskiy.pcs.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.borshchevskiy.pcs.entities.employee.Employee;

import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByAccount(String account);

}
