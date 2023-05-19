package ru.borshchevskiy.pcs.services.employee.impl;

import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.employee.impl.EmployeeJdbcRepositoryImpl;
import ru.borshchevskiy.pcs.services.employee.EmployeeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeServiceImpl implements EmployeeService {


    private final EmployeeRepository repository = new EmployeeJdbcRepositoryImpl();
    private final EmployeeMapper employeeMapper = new EmployeeMapper();


    @Override
    public EmployeeDto getById(Long id) {
        return repository.getById(id)
                .map(employeeMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Employee not found!"));

    }

    @Override
    public List<EmployeeDto> getAll() {
        return repository.getAll()
                .stream()
                .map(employeeMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getByFilter(EmployeeFilter filter) {
        return repository.findByFilter(filter)
                .stream()
                .map(employeeMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto save(EmployeeDto dto) {
        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private EmployeeDto create(EmployeeDto dto) {
        Employee employee = repository.create(employeeMapper.createEmployee(dto));

        return employeeMapper.mapToDto(employee);
    }

    private EmployeeDto update(EmployeeDto dto) {

        Employee employee = repository.getById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Employee not found!"));

        if (employee.getStatus() == EmployeeStatus.DELETED) {
            throw new DeletedItemModificationException("Can't modify deleted objects!");
        }

        employeeMapper.mergeEmployee(employee, dto);

        return employeeMapper.mapToDto(repository.update(employee));
    }

    // Метод только меняет employeeStatus на DELETED, не удаляет файл
    // Если Employee нашелся, меняет статус на DELETED и перезаписывает файл с объектом, возвраащет true
    // Если нет, возвращает false
    @Override
    public boolean deleteById(Long id) {
        Optional<Employee> optionalEmployee = repository.getById(id);

        return optionalEmployee.map(employee ->
                {
                    employee.setStatus(EmployeeStatus.DELETED);
                    return employee;
                })
                .map(employee -> {
                    repository.update(employee);
                    return true;
                })
                .orElse(false);

    }
}
