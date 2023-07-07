package ru.borshchevskiy.pcs.web;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "Swagger auth",
        scheme = "basic",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

    @Value("${openapi.dev-url}")
    private String devUrl;

    @Value("${openapi.prod-url:${openapi.dev-url}}")
    private String prodUrl;

    @Bean
    public OpenAPI openApi() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("freer1de@yandex.ru");
        contact.setName("Ilya");

        return new OpenAPI()
                .info(new Info().description("Project Control System API")
                        .version("0.0.1")
                        .contact(contact)
                        .title("Project Control System"))
                .servers(List.of(devServer, prodServer));

    }
}
