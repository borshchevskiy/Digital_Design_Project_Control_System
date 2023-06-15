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
import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.common.enums.Role;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.web.controllers.integration.IntegrationTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class EmployeeControllerCreateIT extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final EmployeeService employeeService;
    private Account account;

    @BeforeEach
    void prepare() {

        account = new Account();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        account.setUsername("username");
        account.setPassword("password");
        account.setRoles(roles);
        accountRepository.save(account);

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
    public void create() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setFirstname("firstname1");
        dto.setLastname("lastname1");


        EmployeeDto expectedDto = new EmployeeDto();
        expectedDto.setId(1L);
        expectedDto.setFirstname("firstname1");
        expectedDto.setLastname("lastname1");
        expectedDto.setStatus(EmployeeStatus.ACTIVE);

        mockMvc.perform(post("/api/v1/employees")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }

    @Test
    public void accountNotFound() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setFirstname("firstname1");
        dto.setLastname("lastname1");
        dto.setUsername("nonExistingUsername");

        mockMvc.perform(post("/api/v1/employees")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Account with specified username not found."));
    }

    @Test
    public void employeeWithUsernameExists() throws Exception {

        EmployeeDto dto = new EmployeeDto();
        dto.setFirstname("firstname1");
        dto.setLastname("lastname1");
        dto.setUsername("username");

        employeeService.save(dto);

        mockMvc.perform(post("/api/v1/employees")
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Employee already exists for this username!"));
    }
}