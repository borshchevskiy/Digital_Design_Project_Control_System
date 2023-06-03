package ru.borshchevskiy.pcs.service;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import ru.borshchevskiy.pcs.common.config.PasswordEncoderConfig;
import ru.borshchevskiy.pcs.entities.EntityConfig;
import ru.borshchevskiy.pcs.repository.RepositoryConfig;

@SpringBootConfiguration
@EnableAutoConfiguration
@ActiveProfiles("test")
@Import({RepositoryConfig.class, EntityConfig.class, PasswordEncoderConfig.class})
public class ServiceIntegrationTestConfig extends ServiceConfig {
}

