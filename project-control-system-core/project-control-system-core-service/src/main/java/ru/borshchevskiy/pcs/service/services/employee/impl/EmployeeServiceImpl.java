package ru.borshchevskiy.pcs.service.services.employee.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeSpecificationUtil;
import ru.borshchevskiy.pcs.service.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
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
    public EmployeeDto findByUsername(String username) {
        return repository.findByUsername(username)
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

        log.info("Employee id=" + employee.getId() + " created.");

        return employeeMapper.mapToDto(employee);
    }

    private EmployeeDto update(EmployeeDto dto) {

        Employee employee = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Employee not found!"));

        if (employee.getStatus() == EmployeeStatus.DELETED) {
            log.warn("Attempt to modify deleted Employee with id=" + employee.getId());
            throw new DeletedItemModificationException("Can't modify deleted objects!");
        }

        employee = employeeMapper.mergeEmployee(employee, dto);

        log.info("Employee id=" + employee.getId() + " modified.");

        return employeeMapper.mapToDto(repository.save(employee));
    }

    // Если Employee нашелся, меняет статус на DELETED, возвраащет true
    // Если нет, возвращает false
    @Override
    @Transactional
    public EmployeeDto deleteById(Long id) {

        Employee employee = repository.findById(id)
                .map(emp -> {
                            if (emp.getStatus() == EmployeeStatus.DELETED) {
                                log.warn("Attempt to delete already deleted Employee with id=" + emp.getId());
                                throw new DeletedItemModificationException("Employee already deleted!");
                            }
                            emp.setStatus(EmployeeStatus.DELETED);
                            return emp;
                        }
                )
                .map(repository::save)
                .orElseThrow(() -> new NotFoundException("Employee with id=" + id + " not found!"));

        log.info("Employee id=" + employee.getId() + " deleted.");
        return employeeMapper.mapToDto(employee);
    }
}
