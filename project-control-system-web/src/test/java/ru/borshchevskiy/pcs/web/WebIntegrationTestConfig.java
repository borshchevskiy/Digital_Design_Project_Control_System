package ru.borshchevskiy.pcs.web;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import ru.borshchevskiy.pcs.common.config.PasswordEncoderConfig;
import ru.borshchevskiy.pcs.entities.EntityConfig;
import ru.borshchevskiy.pcs.repository.RepositoryConfig;
import ru.borshchevskiy.pcs.service.RabbitConfig;
import ru.borshchevskiy.pcs.service.ServiceConfig;

@SpringBootConfiguration
@EnableAutoConfiguration
@PropertySource("classpath:application-test.properties")
@Import({ServiceConfig.class, RepositoryConfig.class, EntityConfig.class, PasswordEncoderConfig.class, RabbitConfig.class})
public class WebIntegrationTestConfig extends WebConfig {

}

