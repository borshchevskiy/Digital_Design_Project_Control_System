package ru.borshchevskiy.pcs.web.controllers.integration.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.Role;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.web.controllers.integration.IntegrationTestBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class EmployeeControllerUpdateIT extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private Account account1;
    private Account account2;
    private EmployeeDto dto1;
    private EmployeeDto dto2;
    private final List<EmployeeDto> employees = new ArrayList<>();

    @BeforeEach
    void prepare() {

        account1 = new Account();
        account2 = new Account();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        account1.setUsername("username");
        account1.setPassword("password");
        account1.setRoles(roles);

        account2.setUsername("username2");
        account2.setPassword("password2");
        account2.setRoles(roles);

        accountRepository.save(account1);
        accountRepository.save(account2);

        Employee employee1 = new Employee();
        employee1.setFirstname("firstname1");
        employee1.setLastname("lastname1");
        employee1.setAccount(account1);
        employee1.setStatus(EmployeeStatus.ACTIVE);

        Employee employee2 = new Employee();
        employee2.setFirstname("firstname2");
        employee2.setLastname("lastname2");
        employee2.setStatus(EmployeeStatus.ACTIVE);

        Employee employee3 = new Employee();
        employee3.setFirstname("firstname3");
        employee3.setLastname("lastname3");
        employee3.setStatus(EmployeeStatus.DELETED);

        Employee save1 = employeeRepository.save(employee1);
        Employee save2 = employeeRepository.save(employee2);
        Employee save3 = employeeRepository.save(employee3);

        dto1 = employeeMapper.mapToDto(save1);
        dto2 = employeeMapper.mapToDto(save2);

        employees.add(dto1);
        employees.add(dto2);

    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.accounts CASCADE ");
        jdbcTemplate.execute("TRUNCATE TABLE test.public.roles CASCADE ");
        jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE accounts_id_seq RESTART");
        jdbcTemplate.execute("ALTER SEQUENCE roles_account_id_seq RESTART");
    }

    @Test
    public void updateFirstnameLastname() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(1L);
        dto.setFirstname("newFirstname");
        dto.setLastname("newLastname");
        dto.setUsername("username");
        dto.setStatus(EmployeeStatus.ACTIVE);

        mockMvc.perform(put("/api/v1/employees/1")
                        .with(user(account1))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void changeAccount1toAccount2() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(1L);
        dto.setFirstname("newFirstname");
        dto.setLastname("newLastname");
        dto.setUsername("username2");
        dto.setStatus(EmployeeStatus.ACTIVE);

        mockMvc.perform(put("/api/v1/employees/1")
                        .with(user(account1))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void empNotFound() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(Long.MIN_VALUE);
        dto.setFirstname("newFirstname");
        dto.setLastname("newLastname");
        dto.setUsername("username");
        dto.setStatus(EmployeeStatus.ACTIVE);

        mockMvc.perform(put("/api/v1/employees/2")
                        .with(user(account1))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("Employee not found!"));
    }

    @Test
    public void tryToUpdateDeleted() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(3L);
        dto.setFirstname("newFirstname");
        dto.setLastname("newLastname");
        dto.setStatus(EmployeeStatus.ACTIVE);

        mockMvc.perform(put("/api/v1/employees/3")
                        .with(user(account1))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("Can't modify deleted objects!"));
    }

    @Test
    public void tryToUpdateStatus() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(1L);
        dto.setFirstname("newFirstname");
        dto.setLastname("newLastname");
        dto.setUsername("username");
        dto.setStatus(EmployeeStatus.DELETED);

        mockMvc.perform(put("/api/v1/employees/1")
                        .with(user(account1))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("Employee status can't be changed!"));
    }

    @Test
    public void tryToChangeUsernameButItHasEmployee() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(2L);
        dto.setFirstname("newFirstname");
        dto.setLastname("newLastname");
        dto.setUsername("username");
        dto.setStatus(EmployeeStatus.ACTIVE);

        mockMvc.perform(put("/api/v1/employees/1")
                        .with(user(account1))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("Employee already exists for this username!"));
    }
}