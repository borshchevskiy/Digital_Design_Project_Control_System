package ru.borshchevskiy.pcs.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import ru.borshchevskiy.pcs.common.config.PasswordEncoderConfig;
import ru.borshchevskiy.pcs.entities.EntityConfig;
import ru.borshchevskiy.pcs.repository.RepositoryConfig;
import ru.borshchevskiy.pcs.service.services.email.impl.EmailServiceImpl;

@SpringBootConfiguration
@EnableAutoConfiguration
@PropertySource("classpath:application-test.properties")
@Import({RepositoryConfig.class,
        EntityConfig.class,
        PasswordEncoderConfig.class,
        EmailServiceImpl.class,
        RabbitConfig.class})
public class ServiceIntegrationTestConfig extends ServiceConfig {

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres");

    private static final RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq")
                    .withExposedPorts(5672);

    @BeforeAll
    static void startContainers() {
        postgresContainer.start();
        rabbitMQContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        postgresContainer.stop();
        rabbitMQContainer.stop();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
//        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
    }
}

