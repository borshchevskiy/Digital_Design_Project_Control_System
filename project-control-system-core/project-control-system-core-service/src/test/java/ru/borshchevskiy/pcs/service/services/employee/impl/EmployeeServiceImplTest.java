package ru.borshchevskiy.pcs.service.services.employee.impl;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeSpecificationUtil;
import ru.borshchevskiy.pcs.service.mappers.employee.EmployeeMapper;

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
        @DisplayName("Testcase id: EmployeeService-1")
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

            assertThat(actualResult.getId()).isEqualTo(testId);
            assertThat(actualResult).isEqualTo(employeeDto);

        }

        @Test
        @DisplayName("Testcase id: EmployeeService-2")
        void employeeDoesntExist() {
            final Long id = 1L;

            doReturn(Optional.empty()).when(repository).findById(anyLong());
            assertThrows(NotFoundException.class, () -> employeeService.findById(id));
        }
    }

    @Nested
    class FindAll {

        @Test
        @DisplayName("Testcase id: EmployeeService-3")
        void employeesExist() {
            final Employee employee1 = new Employee();
            final Employee employee2 = new Employee();
            final Employee employee3 = new Employee();

            final List<Employee> employeeList = List.of(employee1, employee2, employee3);

            doReturn(employeeList).when(repository).findAll();
            doReturn(new EmployeeDto()).when(employeeMapper).mapToDto(any(Employee.class));

            List<EmployeeDto> actualEmployees = employeeService.findAll();

            assertNotNull(actualEmployees);
            assertThat(actualEmployees.size()).isEqualTo(employeeList.size());
        }

        @Test
        @DisplayName("Testcase id: EmployeeService-4")
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
        @DisplayName("Testcase id: EmployeeService-5")
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
        @DisplayName("Testcase id: EmployeeService-6")
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
    class FindByUsername {

        @Test
        @DisplayName("Testcase id: EmployeeService-7")
        void employeeExists() {
            final Long id = 1L;
            final String name = "testName";
            final Account account = new Account();
            final String username = "username";
            account.setUsername(username);

            final Employee employee = new Employee();
            employee.setId(id);
            employee.setFirstname(name);
            employee.setAccount(account);

            final EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(id);
            employeeDto.setFirstname(name);
            employeeDto.setUsername(account.getUsername());


            doReturn(Optional.of(employee)).when(repository).findByUsername(username);
            doReturn(employeeDto).when(employeeMapper).mapToDto(employee);

            EmployeeDto actualResult = employeeService.findByUsername(username);

            assertNotNull(actualResult);
            assertThat(actualResult.getUsername()).isEqualTo(employeeDto.getUsername());
        }

        @Test
        @DisplayName("Testcase id: EmployeeService-8")
        void employeeDoesntExist() {
            final String username = "testAccount";

            doReturn(Optional.empty()).when(repository).findByUsername(username);
            assertThrows(NotFoundException.class,
                    () -> employeeService.findByUsername(username));
        }

    }

    @Nested
    class Save {

        @Test
        @DisplayName("Testcase id: EmployeeService-9")
        void createEmployee() {
            final Long id = 1L;
            final String name = "testName";
            final String username = "username";
            final Account account = new Account();
            account.setUsername(username);

            final Employee unSavedEmployee = new Employee();
            unSavedEmployee.setFirstname(name);
            unSavedEmployee.setAccount(account);

            final Employee savedEmployee = new Employee();
            savedEmployee.setId(id);
            savedEmployee.setFirstname(name);
            savedEmployee.setAccount(account);

            final EmployeeDto requestDto = new EmployeeDto();
            requestDto.setFirstname(name);
            requestDto.setUsername(username);

            final EmployeeDto responseDto = new EmployeeDto();
            responseDto.setId(id);
            responseDto.setFirstname(name);
            responseDto.setUsername(username);

            doReturn(savedEmployee).when(repository).save(unSavedEmployee);
            doReturn(unSavedEmployee).when(employeeMapper).createEmployee(requestDto);
            doReturn(responseDto).when(employeeMapper).mapToDto(savedEmployee);

            EmployeeDto actualResult = employeeService.save(requestDto);


            assertNotNull(actualResult);
            assertNotNull(actualResult.getId());
            assertThat(actualResult.getUsername()).isEqualTo(username);
            assertThat(actualResult.getFirstname()).isEqualTo(name);

        }

        @Test
        @DisplayName("Testcase id: EmployeeService-10")
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
        @DisplayName("Testcase id: EmployeeService-11")
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
        @DisplayName("Testcase id: EmployeeService-12")
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
        @DisplayName("Testcase id: EmployeeService-13")
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
        @DisplayName("Testcase id: EmployeeService-14")
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
        @DisplayName("Testcase id: EmployeeService-15")
        void employeeNotFound() {
            final Long id = 1L;

            doReturn(Optional.empty()).when(repository).findById(anyLong());

            assertThrows(NotFoundException.class, () -> employeeService.deleteById(id));
        }
    }

}