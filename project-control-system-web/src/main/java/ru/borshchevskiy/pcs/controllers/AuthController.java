package ru.borshchevskiy.pcs.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.login.LoginDto;
import ru.borshchevskiy.pcs.services.account.AccountService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @Operation(summary = "Регистрация", description = "Зарегистрировать нового сотрудника")
    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public AccountDto register(@RequestBody AccountDto request) {

        return accountService.save(request);
    }

    @Operation(summary = "Вход", description = "Аутентифицироваться в приложении")
    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE)
    public void login(@RequestBody LoginDto credentials,
                                        HttpServletRequest request) throws ServletException {

        request.login(credentials.username(), credentials.password());
    }

    @Operation(summary = "Выход", description = "Выход из приложения")
    @PostMapping(value = "/logout")
    public void logout(HttpServletRequest request) throws ServletException {

        request.logout();
    }

}
