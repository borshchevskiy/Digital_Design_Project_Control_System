package ru.borshchevskiy.pcs.services.employee.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeSpecificationUtil;
import ru.borshchevskiy.pcs.services.employee.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        return repository.findById(id)
                .map(employeeMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));

    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDto> findAll() {
        return repository.findAll()
                .stream()
                .map(employeeMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDto> findAllByFilter(EmployeeFilter filter) {
        return repository.findAll(EmployeeSpecificationUtil.getSpecification(filter))
                .stream()
                .map(employeeMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto findByAccount(String account) {
        return repository.findByAccount(account)
                .map(employeeMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Employee not found!"));

    }

    @Override
    @Transactional
    public EmployeeDto save(EmployeeDto dto) {
        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private EmployeeDto create(EmployeeDto dto) {
        Employee employee = repository.save(employeeMapper.createEmployee(dto));

        return employeeMapper.mapToDto(employee);
    }

    private EmployeeDto update(EmployeeDto dto) {

        Employee employee = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Employee not found!"));

        if (employee.getStatus() == EmployeeStatus.DELETED) {
            throw new DeletedItemModificationException("Can't modify deleted objects!");
        }

        employeeMapper.mergeEmployee(employee, dto);

        return employeeMapper.mapToDto(repository.save(employee));
    }

    // Если Employee нашелся, меняет статус на DELETED, возвраащет true
    // Если нет, возвращает false
    @Override
    @Transactional
    public EmployeeDto deleteById(Long id) {

        return repository.findById(id)
                .map(employee -> {
                            if (employee.getStatus() == EmployeeStatus.DELETED) {
                                throw new DeletedItemModificationException("Employee already deleted!");
                            }
                            employee.setStatus(EmployeeStatus.DELETED);
                            return employee;
                        }
                )
                .map(repository::save)
                .map(employeeMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));

    }
}
