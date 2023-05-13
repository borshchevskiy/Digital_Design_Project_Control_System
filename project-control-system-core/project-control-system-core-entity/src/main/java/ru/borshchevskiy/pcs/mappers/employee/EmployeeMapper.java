package ru.borshchevskiy.pcs.mappers.employee;

import ru.borshchevskiy.pcs.dto.employee.request.EmployeeCreateDto;
import ru.borshchevskiy.pcs.dto.employee.request.EmployeeUpdateDto;
import ru.borshchevskiy.pcs.dto.employee.response.EmployeeReadDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;

public class EmployeeMapper {

    public EmployeeReadDto mapToReadDto(Employee employee) {
        EmployeeReadDto employeeReadDto = new EmployeeReadDto();

        employeeReadDto.setFirstname(employee.getFirstname());
        employeeReadDto.setLastname(employee.getLastname());
        employeeReadDto.setPatronymic(employee.getPatronymic());
        employeeReadDto.setDisplayName(String.join(" ", new String[]{employee.getFirstname(), employee.getLastname()}));
        employeeReadDto.setPosition(employee.getPosition());
        employeeReadDto.setAccount(employee.getAccount());
        employeeReadDto.setEmail(employee.getEmail());
        employeeReadDto.setStatus(employee.getStatus());

        return employeeReadDto;
    }

    public Employee createEmployee(EmployeeCreateDto dto) {
        Employee employee = new Employee();

        employee.setFirstname(dto.getFirstname());
        employee.setLastname(dto.getLastname());
        employee.setPatronymic(dto.getPatronymic());
        employee.setPosition(dto.getPosition());
        employee.setAccount(dto.getAccount());
        employee.setEmail(dto.getEmail());
        employee.setStatus(dto.getStatus());

        return employee;
    }

    public Employee updateEmployee(Employee employee, EmployeeUpdateDto dto) {

        employee.setFirstname(dto.getFirstname());
        employee.setLastname(dto.getLastname());
        employee.setPatronymic(dto.getPatronymic());
        employee.setPosition(dto.getPosition());
        employee.setAccount(dto.getAccount());
        employee.setEmail(dto.getEmail());
        employee.setStatus(dto.getStatus());

        return employee;
    }
}
