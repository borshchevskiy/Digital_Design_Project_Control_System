package ru.borshchevskiy.pcs.service.mappers.employee;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final AccountRepository accountRepository;

    public EmployeeDto mapToDto(Employee employee) {
        EmployeeDto employeeDto = new EmployeeDto();

        employeeDto.setId(employee.getId());
        employeeDto.setFirstname(employee.getFirstname());
        employeeDto.setLastname(employee.getLastname());
        employeeDto.setPatronymic(employee.getPatronymic());
        employeeDto.setPosition(employee.getPosition());
        employeeDto.setUsername(Optional.ofNullable(employee.getAccount()).map(Account::getUsername).orElse(null));
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

    public Employee mergeEmployee(Employee employee, EmployeeDto dto) {

        return copyToEmployee(employee, dto);
    }

    private Account getAccount(String username) {
        return Optional.ofNullable(username)
                .flatMap(accountRepository::findByUsername)
                .orElse(null);
    }

    private Employee copyToEmployee(Employee copyTo, EmployeeDto copyFrom) {

        copyTo.setFirstname(copyFrom.getFirstname());
        copyTo.setLastname(copyFrom.getLastname());
        copyTo.setPatronymic(copyFrom.getPatronymic());
        copyTo.setPosition(copyFrom.getPosition());
        copyTo.setAccount(getAccount(copyFrom.getUsername()));
        copyTo.setEmail(copyFrom.getEmail());
        copyTo.setStatus(copyFrom.getStatus());

        return copyTo;
    }


}
