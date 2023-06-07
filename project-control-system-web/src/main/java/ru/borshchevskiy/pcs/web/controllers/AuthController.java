package ru.borshchevskiy.pcs.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.borshchevskiy.pcs.common.exceptions.AuthException;
import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.dto.login.LoginDto;
import ru.borshchevskiy.pcs.service.services.account.AccountService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
                      HttpServletRequest request) {
        try {
            request.login(credentials.username(), credentials.password());
        } catch (ServletException e) {
            throw new AuthException(e.getMessage(), e);
        }
    }

    @Operation(summary = "Выход", description = "Выход из приложения")
    @PostMapping(value = "/logout")
    public void logout(HttpServletRequest request) {
        try {
            request.logout();
        } catch (ServletException e) {
            throw new AuthException(e.getMessage(), e);
        }
    }

}
