package ru.borshchevskiy.pcs.web.controllers.integration.auth;

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
import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.dto.login.LoginDto;
import ru.borshchevskiy.pcs.service.services.account.AccountService;
import ru.borshchevskiy.pcs.web.controllers.integration.IntegrationTestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AuthControllerLoginIT extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final AccountService accountService;

    @BeforeEach
    void prepare() {
        AccountDto account = new AccountDto();
        account.setUsername("username");
        account.setFirstname("firstname");
        account.setLastname("lastname");
        account.setPassword("password");

        accountService.save(account);
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
    public void login() throws Exception {

        LoginDto credentials = new LoginDto("username", "password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void badCredentials() throws Exception {

        LoginDto credentials = new LoginDto("username", "badPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Bad credentials"));
    }

}