package ru.borshchevskiy.pcs.services.employee.impl;

import ru.borshchevskiy.pcs.dto.employee.request.EmployeeCreateDto;
import ru.borshchevskiy.pcs.dto.employee.request.EmployeeUpdateDto;
import ru.borshchevskiy.pcs.dto.employee.response.EmployeeReadDto;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.employee.impl.EmployeeRepositoryImpl;
import ru.borshchevskiy.pcs.services.employee.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeServiceImpl implements EmployeeService {


    private final EmployeeRepository repository = new EmployeeRepositoryImpl();
    private final EmployeeMapper employeeMapper = new EmployeeMapper();


    @Override
    public EmployeeReadDto getById(Long id) {
        return repository.getById(id)
                .map(employeeMapper::mapToReadDto)
                .orElseGet(EmployeeReadDto::new);
//        TODO: В дальнейшем заменить эту строку на выброс исключения.
//         Сейчас если объект не найден, то отправляет пустой ДТО для отладки.
//                .orElseThrow(() -> new NotFoundException("Employee not found!"));

    }

    @Override
    public List<EmployeeReadDto> getAll() {
        return repository.getAll()
                .stream()
                .map(employeeMapper::mapToReadDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeReadDto create(EmployeeCreateDto createDto) {
        createDto.setStatus(EmployeeStatus.ACTIVE);
        Employee employee = employeeMapper.createEmployee(createDto);
        return employeeMapper.mapToReadDto(repository.create(employee));
    }

    @Override
    public EmployeeReadDto update(EmployeeUpdateDto updateDto) {
        Employee employee = repository.getById(updateDto.getId())
                .orElseThrow(() -> new NotFoundException("Employee not found!"));

//        TODO: Этот try-catch больше для отладки, пересмотреть код метода при добавлении JPA
        try {
            if (employee.getStatus() == EmployeeStatus.DELETED) {
                throw new RuntimeException("Can't modify deleted objects!");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            // Возвращает пустой DTO для отладки
            return new EmployeeReadDto();
        }

        employee = employeeMapper.updateEmployee(employee, updateDto);

        return employeeMapper.mapToReadDto(repository.update(employee));

    }

    // Метод только меняет employeeStatus на DELETED
    @Override
    public boolean deleteById(Long id) {
        return repository.getById(id)
                .map(employee -> {
                            employee.setStatus(EmployeeStatus.DELETED);
                            return employee;
                        }
                )
                .map(entity -> {
                    repository.delete(entity);
                    return true;
                })
                .orElse(false);
    }
}
