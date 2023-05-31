package ru.borshchevskiy.pcs.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.borshchevskiy.pcs.entities.employee.Employee;

import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    @Query("SELECT e FROM Employee e JOIN FETCH e.account a WHERE a.username = :username")
    Optional<Employee> findByUsername(@Param("username") String username);

}
