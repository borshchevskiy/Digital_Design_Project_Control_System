package ru.borshchevskiy.pcs.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ru.borshchevskiy.pcs")
@EnableJpaRepositories(basePackages = "ru.borshchevskiy.pcs.repository")
@EntityScan(basePackages = "ru.borshchevskiy.pcs.entities")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
