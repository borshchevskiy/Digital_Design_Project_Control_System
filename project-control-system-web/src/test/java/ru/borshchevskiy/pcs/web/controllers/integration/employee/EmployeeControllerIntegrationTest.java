package ru.borshchevskiy.pcs.web.controllers.integration.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.enums.Role;
import ru.borshchevskiy.pcs.repository.account.AccountRepository;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;
import ru.borshchevskiy.pcs.service.mappers.employee.EmployeeMapper;
import ru.borshchevskiy.pcs.service.services.employee.EmployeeService;
import ru.borshchevskiy.pcs.web.controllers.integration.IntegrationTestBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class EmployeeControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;



    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Get {

        private final Account account = new Account();
        private final List<EmployeeDto> employees = new ArrayList<>();
        private EmployeeDto dto1;
        private EmployeeDto dto2;

        @BeforeAll
        void prepare() {
            account.setUsername("username");
            account.setPassword("password");
            account.setRoles(Collections.singleton(Role.USER));
            accountRepository.save(account);

            Employee employee1 = new Employee();
            employee1.setFirstname("firstname1");
            employee1.setLastname("lastname1");
            employee1.setStatus(EmployeeStatus.ACTIVE);

            Employee employee2 = new Employee();
            employee2.setFirstname("firstname2");
            employee2.setLastname("lastname2");
            employee2.setStatus(EmployeeStatus.ACTIVE);

            dto1 = employeeMapper.mapToDto(employeeRepository.save(employee1));
            dto2 = employeeMapper.mapToDto(employeeRepository.save(employee2));

            employees.add(dto1);
            employees.add(dto2);

        }

        @AfterAll
        void cleanDatabase() {
            jdbcTemplate.execute("TRUNCATE TABLE test.public.employees CASCADE ");
            jdbcTemplate.execute("ALTER SEQUENCE employees_id_seq RESTART");
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
    }


}