package ru.borshchevskiy.pcs.services.employee.impl;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeSpecificationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {


    @Mock
    private EmployeeRepository repository;
    @Mock
    private EmployeeMapper employeeMapper;
    @InjectMocks
    private EmployeeServiceImpl employeeService;


    @Nested
    class FindById {
        @Test
        void employeeExists() {
            final Long testId = 1L;
            final String testName = "testName";

            final Employee employee = new Employee();
            employee.setId(testId);
            employee.setFirstname(testName);

            final EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(testId);
            employeeDto.setFirstname(testName);

            doReturn(Optional.of(employee)).when(repository).findById(testId);
            doReturn(employeeDto).when(employeeMapper).mapToDto(employee);

            EmployeeDto actualResult = employeeService.findById(testId);

            assertNotNull(actualResult);

            assertThat(actualResult.getId()).isEqualTo(employeeDto.getId());
            assertThat(actualResult).isEqualTo(employeeDto);

        }

        @Test
        void employeeDoesntExist() {
            final Long id = 1L;

            doReturn(Optional.empty()).when(repository).findById(anyLong());
            assertThrows(NotFoundException.class, () -> employeeService.findById(id));
        }
    }

    @Nested
    class FindAll {

        @Test
        void employeesExist() {
            final List<Employee> employeeList = List.of(new Employee(), new Employee(), new Employee());
            doReturn(employeeList).when(repository).findAll();

            List<EmployeeDto> actualEmployees = employeeService.findAll();

            assertNotNull(actualEmployees);
            assertThat(employeeList.size()).isEqualTo(actualEmployees.size());
        }

        @Test
        void employeesDontExist() {

            doReturn(new ArrayList<>()).when(repository).findAll();

            List<EmployeeDto> actualEmployees = employeeService.findAll();

            assertNotNull(actualEmployees);
            assertTrue(actualEmployees.isEmpty());
        }
    }

    @Nested
    class FindAllByFilter {

        @Test
        void usersFound() {
            final List<Employee> employeeList = List.of(new Employee(), new Employee(), new Employee());
            EmployeeFilter employeeFilter = new EmployeeFilter("testValue");

            try (MockedStatic<EmployeeSpecificationUtil> mocked = mockStatic(EmployeeSpecificationUtil.class)) {
                mocked.when(() -> EmployeeSpecificationUtil.getSpecification(employeeFilter))
                        .thenReturn((Specification<Employee>) (root, query, criteriaBuilder) -> null);
            }

            doReturn(employeeList).when(repository).findAll(any(Specification.class));

            List<EmployeeDto> actualEmployees = employeeService.findAllByFilter(employeeFilter);

            assertNotNull(actualEmployees);
            assertThat(employeeList.size()).isEqualTo(actualEmployees.size());
        }

        @Test
        void usersNotFound() {

            EmployeeFilter employeeFilter = new EmployeeFilter("testValue");

            try (MockedStatic<EmployeeSpecificationUtil> mocked = mockStatic(EmployeeSpecificationUtil.class)) {
                mocked.when(() -> EmployeeSpecificationUtil.getSpecification(employeeFilter))
                        .thenReturn((Specification<Employee>) (root, query, criteriaBuilder) -> null);
            }

            doReturn(new ArrayList<>()).when(repository).findAll(any(Specification.class));

            List<EmployeeDto> actualEmployees = employeeService.findAllByFilter(employeeFilter);

            assertNotNull(actualEmployees);
            assertTrue(actualEmployees.isEmpty());
        }
    }

    @Nested
    class FindByAccount {

        @Test
        void employeeExists() {
            final Long id = 1L;
            final String name = "testName";
            final String account = "testAccount";

            final Employee employee = new Employee();
            employee.setId(id);
            employee.setFirstname(name);
            employee.setAccount(account);

            final EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(id);
            employeeDto.setFirstname(name);
            employeeDto.setAccount(account);


            doReturn(Optional.of(employee)).when(repository).findByAccount(account);
            doReturn(employeeDto).when(employeeMapper).mapToDto(employee);

            EmployeeDto actualResult = employeeService.findByUsername(account);

            assertNotNull(actualResult);

            assertThat(actualResult.getFirstname()).isEqualTo(employeeDto.getFirstname());
        }

        @Test
        void employeeDoesntExist() {
            final String account = "testAccount";

            doReturn(Optional.empty()).when(repository).findByAccount(account);
            assertThrows(NotFoundException.class,
                    () -> employeeService.findByUsername(account));
        }

    }

    @Nested
    class Save {

        @Test
        void createEmployeeWithAccountProvided() {
            final Long id = 1L;
            final String name = "testName";
            final String account = "testAccount";

            final Employee unSavedEmployee = new Employee();
            unSavedEmployee.setFirstname(name);
            unSavedEmployee.setAccount(account);

            final Employee savedEmployee = new Employee();
            savedEmployee.setId(id);
            savedEmployee.setFirstname(name);
            savedEmployee.setAccount(account);

            final EmployeeDto requestDto = new EmployeeDto();
            requestDto.setFirstname(name);
            requestDto.setAccount(account);

            final EmployeeDto responseDto = new EmployeeDto();
            responseDto.setId(id);
            responseDto.setFirstname(name);
            responseDto.setAccount(account);

            doReturn(savedEmployee).when(repository).save(unSavedEmployee);
            doReturn(unSavedEmployee).when(employeeMapper).createEmployee(requestDto);
            doReturn(responseDto).when(employeeMapper).mapToDto(savedEmployee);

            EmployeeDto actualResult = employeeService.save(requestDto);


            assertNotNull(actualResult);
            assertNotNull(actualResult.getId());
            assertThat(actualResult.getAccount()).isEqualTo(account);
            assertThat(actualResult).isEqualTo(responseDto);

        }

        @Test
        void createEmployeeWithNoAccountProvided() {
            final Long id = 1L;
            final String firstname = "firstname";
            final String lastname = "lastname";
            final String generatedAccount = firstname + lastname + id;

            final Employee unSavedEmployee = new Employee();
            unSavedEmployee.setFirstname(firstname);
            unSavedEmployee.setLastname(lastname);

            final Employee savedEmployee = new Employee();
            savedEmployee.setId(id);
            savedEmployee.setFirstname(firstname);
            savedEmployee.setLastname(lastname);

            final EmployeeDto requestDto = new EmployeeDto();
            requestDto.setFirstname(firstname);
            requestDto.setLastname(lastname);

            final EmployeeDto responseDto = new EmployeeDto();
            responseDto.setId(id);
            responseDto.setFirstname(firstname);
            responseDto.setLastname(lastname);
            responseDto.setAccount(generatedAccount);

            doReturn(savedEmployee).when(repository).save(unSavedEmployee);
            doReturn(unSavedEmployee).when(employeeMapper).createEmployee(requestDto);
            doReturn(responseDto).when(employeeMapper).mapToDto(savedEmployee);

            EmployeeDto actualResult = employeeService.save(requestDto);

            assertNotNull(actualResult);
            assertNotNull(actualResult.getId());
            assertThat(actualResult.getAccount()).isEqualTo(generatedAccount);
            assertThat(savedEmployee.getAccount()).isEqualTo(generatedAccount);
            assertThat(actualResult).isEqualTo(responseDto);

        }

        @Test
        void updateSuccess() {
            final Long id = 1L;
            final String firstname = "firstname";
            final String newFirstname = "newFirstname";

            final Employee employee = new Employee();
            employee.setId(id);
            employee.setFirstname(firstname);

            final Employee updatedEmployee = new Employee();
            updatedEmployee.setId(id);
            updatedEmployee.setFirstname(newFirstname);

            final EmployeeDto requestDto = new EmployeeDto();
            requestDto.setId(id);
            requestDto.setFirstname(newFirstname);

            final EmployeeDto responseDto = new EmployeeDto();
            responseDto.setId(id);
            responseDto.setFirstname(newFirstname);

            doReturn(Optional.of(employee)).when(repository).findById(id);
            doReturn(updatedEmployee).when(repository).save(updatedEmployee);
            doReturn(responseDto).when(employeeMapper).mapToDto(updatedEmployee);
            doReturn(updatedEmployee).when(employeeMapper).mergeEmployee(employee, requestDto);

            EmployeeDto actualResult = employeeService.save(requestDto);

            assertNotNull(actualResult);
            assertThat(actualResult.getId()).isEqualTo(id);
            assertThat(actualResult.getFirstname()).isEqualTo(newFirstname);
        }

        @Test
        void updateEmployeeNotFound() {
            final Long id = 1L;
            final String newFirstname = "newFirstname";

            final EmployeeDto requestDto = new EmployeeDto();
            requestDto.setId(id);
            requestDto.setFirstname(newFirstname);

            doReturn(Optional.empty()).when(repository).findById(anyLong());

            assertThrows(NotFoundException.class, () -> employeeService.save(requestDto));
        }

        @Test
        void updateDeletedEmployeeFail() {
            final Long id = 1L;
            final String firstname = "firstname";
            final String newFirstname = "newFirstname";

            final Employee employee = new Employee();
            employee.setId(id);
            employee.setFirstname(firstname);
            employee.setStatus(EmployeeStatus.DELETED);

            final EmployeeDto requestDto = new EmployeeDto();
            requestDto.setId(id);
            requestDto.setFirstname(newFirstname);

            doReturn(Optional.of(employee)).when(repository).findById(id);

            assertThrows(DeletedItemModificationException.class, () -> employeeService.save(requestDto));
        }
    }

    @Nested
    class Delete {

        @Test
        void deleteSuccess() {
            final Long id = 1L;
            final EmployeeStatus active = EmployeeStatus.ACTIVE;
            final EmployeeStatus deleted = EmployeeStatus.DELETED;

            final Employee employee = new Employee();
            employee.setId(id);
            employee.setStatus(active);

            final Employee deletedEmployee = new Employee();
            deletedEmployee.setId(id);
            deletedEmployee.setStatus(deleted);

            final EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(id);
            employeeDto.setStatus(deleted);

            doReturn(Optional.of(employee)).when(repository).findById(id);
            doReturn(deletedEmployee).when(repository).save(deletedEmployee);
            doReturn(employeeDto).when(employeeMapper).mapToDto(deletedEmployee);

            EmployeeDto actualResult = employeeService.deleteById(id);

            assertNotNull(actualResult);
            assertThat(actualResult.getId()).isEqualTo(employeeDto.getId());
            assertThat(actualResult.getStatus()).isEqualTo(EmployeeStatus.DELETED);
        }

        @Test
        void alreadyDeleted() {
            final Long id = 1L;
            final EmployeeStatus deleted = EmployeeStatus.DELETED;

            final Employee employee = new Employee();
            employee.setId(id);
            employee.setStatus(deleted);

            doReturn(Optional.of(employee)).when(repository).findById(id);

            assertThrows(DeletedItemModificationException.class, () -> employeeService.deleteById(id));
        }

        @Test
        void employeeNotFound() {
            final Long id = 1L;

            doReturn(Optional.empty()).when(repository).findById(anyLong());

            assertThrows(NotFoundException.class, () -> employeeService.deleteById(id));
        }
    }

}