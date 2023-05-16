package ru.borshchevskiy.pcs.mappers.employee;


import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;

public class EmployeeMapper {

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

        return employeeDto;
    }

    public Employee createEmployee(EmployeeDto dto) {
        Employee employee = new Employee();

        copyToEmployee(employee, dto);

        employee.setStatus(EmployeeStatus.ACTIVE);

        return employee;
    }

    public void mergeEmployee(Employee employee, EmployeeDto dto) {

        copyToEmployee(employee, dto);
    }

    private static void copyToEmployee(Employee copyTo, EmployeeDto copyFrom) {
        copyTo.setFirstname(copyFrom.getFirstname());
        copyTo.setLastname(copyFrom.getLastname());
        copyTo.setPatronymic(copyFrom.getPatronymic());
        copyTo.setPosition(copyFrom.getPosition());
        copyTo.setAccount(copyFrom.getAccount());
        copyTo.setEmail(copyFrom.getEmail());
        copyTo.setStatus(copyFrom.getStatus());
    }


}
