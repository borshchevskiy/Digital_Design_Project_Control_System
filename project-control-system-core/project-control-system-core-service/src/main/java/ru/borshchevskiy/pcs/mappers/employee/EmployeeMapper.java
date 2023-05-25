package ru.borshchevskiy.pcs.mappers.employee;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.enums.Role;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final PasswordEncoder passwordEncoder;

    public EmployeeDto mapToDto(Employee employee) {
        EmployeeDto employeeDto = new EmployeeDto();

        employeeDto.setId(employee.getId());
        employeeDto.setFirstname(employee.getFirstname());
        employeeDto.setLastname(employee.getLastname());
        employeeDto.setPatronymic(employee.getPatronymic());
        employeeDto.setPosition(employee.getPosition());
        employeeDto.setAccount(employee.getAccount());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setStatus(employee.getStatus());
        employeeDto.setPassword(null);
        employeeDto.setRoles(employee.getRoles());

        return employeeDto;
    }

    public Employee createEmployee(EmployeeDto dto) {
        Employee employee = new Employee();

        copyToEmployee(employee, dto);

        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setRoles(Collections.singleton(Role.USER));

        return employee;
    }

    public void mergeEmployee(Employee employee, EmployeeDto dto) {

        copyToEmployee(employee, dto);
    }

    private void copyToEmployee(Employee copyTo, EmployeeDto copyFrom) {
        copyTo.setFirstname(copyFrom.getFirstname());
        copyTo.setLastname(copyFrom.getLastname());
        copyTo.setPatronymic(copyFrom.getPatronymic());
        copyTo.setPosition(copyFrom.getPosition());
        copyTo.setAccount(copyFrom.getAccount());
        copyTo.setEmail(copyFrom.getEmail());
        copyTo.setStatus(copyFrom.getStatus());
        copyTo.setPassword(passwordEncoder.encode(copyFrom.getPassword()));
        copyTo.setRoles(copyFrom.getRoles());
    }


}
