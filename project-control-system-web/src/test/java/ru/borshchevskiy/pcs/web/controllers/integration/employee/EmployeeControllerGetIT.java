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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class EmployeeControllerGetIT extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private Account account;
    private EmployeeDto dto1;
    private EmployeeDto dto2;
    private final List<EmployeeDto> employees = new ArrayList<>();

    @BeforeEach
    void prepare() {

        account = new Account();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        account.setUsername("username");
        account.setPassword("password");
        account.setRoles(roles);
        accountRepository.save(account);

        Employee employee1 = new Employee();
        employee1.setFirstname("firstname1");
        employee1.setLastname("lastname1");
        employee1.setAccount(account);
        employee1.setStatus(EmployeeStatus.ACTIVE);

        Employee employee2 = new Employee();
        employee2.setFirstname("firstname2");
        employee2.setLastname("lastname2");
        employee2.setStatus(EmployeeStatus.ACTIVE);

        Employee save1 = employeeRepository.save(employee1);
        Employee save2 = employeeRepository.save(employee2);

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
    public void getEmployees() throws Exception {
        mockMvc.perform(get("/api/v1/employees")
                        .with(user(account)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(employees)));
    }

    @Test
    public void getEmployee() throws Exception {
        mockMvc.perform(get("/api/v1/employees/1")
                        .with(user(account)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(dto1)));
    }

    @Test
    public void getByUsername() throws Exception {
        mockMvc.perform(get("/api/v1/employees/usernames/username")
                        .with(user(account)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(dto1)));
    }

}