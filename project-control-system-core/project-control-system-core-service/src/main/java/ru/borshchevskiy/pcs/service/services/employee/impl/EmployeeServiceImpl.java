package ru.borshchevskiy.pcs.service.services.employee.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeSpecificationUtil;
import ru.borshchevskiy.pcs.service.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper employeeMapper;
    private final AccountRepository accountRepository;


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
        // Т.к. firstname и lastname обязательные, проверяем их наличие
        if (!StringUtils.hasText(dto.getFirstname()) || !StringUtils.hasText(dto.getLastname())) {
            throw new RequestDataValidationException("Firstname and Lastname can't be empty");
        }

        return dto.getId() == null
                ? create(dto)
                : update(dto);
    }

    private EmployeeDto create(EmployeeDto dto) {

        // Проверяем, если в dto указана учетная запись, то проверяем, существует ли она.
        // Если такой учетной записи нет отправляем Exception
        if (dto.getUsername() != null && accountRepository.findByUsername(dto.getUsername()).isEmpty()) {
            throw new NotFoundException("Account with specified username not found.");
        }

        // Проверяем есть ли уже Employee для этой учетной записи
        // Если есть и он ACTIVE, бросаем Exception
        Employee employeeByUsername = repository.findByUsername(dto.getUsername()).orElse(null);

        if (employeeByUsername != null && employeeByUsername.getStatus() != EmployeeStatus.DELETED) {
            throw new RequestDataValidationException("Employee already exists for this username!");
        }

        // Проверки пройдены, создаем Employee
        Employee employee = repository.save(employeeMapper.createEmployee(dto));

        log.debug("Employee id=" + employee.getId() + " created.");

        return employeeMapper.mapToDto(employee);
    }

    private EmployeeDto update(EmployeeDto dto) {

        Employee employee = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Employee not found!"));

        // Изменить удаленного сотрудника нельзя
        if (employee.getStatus() == EmployeeStatus.DELETED) {
            log.error("Attempt to modify deleted Employee with id=" + employee.getId());
            throw new DeletedItemModificationException("Can't modify deleted objects!");
        }

        // Статус обязателен и не может быть null или изменен при изменении сотрудника
        if (dto.getStatus() != employee.getStatus()) {
            throw new RequestDataValidationException("Employee status can't be changed!");
        }

        // Если username из запроса не пуст и отличается от username у изменяемого Employee, то нужно проверить
        // существует ли уже Employee с username из запроса. Если существует, то мы не можем поменять username, т.к.
        // получим 2 Employee с одинаковым username.
        if (dto.getUsername() != null
            && !dto.getUsername().equals(Optional.ofNullable(employee.getAccount())
                .map(Account::getUsername)
                .orElse(null))) {

            // Ищем Employee по username, если нашелся, бросаем исключение
            if (repository.findByUsername(dto.getUsername()).isPresent()) {
                throw new RequestDataValidationException("Employee already exists for this username!");
            }
        }

        // Все проверки пройдены, обновляем Employee
        employee = employeeMapper.mergeEmployee(employee, dto);

        log.debug("Employee id=" + employee.getId() + " modified.");

        return employeeMapper.mapToDto(repository.save(employee));
    }

    // При удалении только меняется статус на DELETED, запись из БД не удаляется
    @Override
    @Transactional
    public EmployeeDto deleteById(Long id) {

        Employee employee = repository.findById(id).map(emp ->
                        {
                            if (emp.getStatus() == EmployeeStatus.DELETED) {
                                log.error("Attempt to delete already deleted Employee with id=" + emp.getId());
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
