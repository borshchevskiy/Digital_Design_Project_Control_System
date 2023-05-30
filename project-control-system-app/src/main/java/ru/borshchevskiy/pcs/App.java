package ru.borshchevskiy.pcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import ru.borshchevskiy.pcs.repository.RepositoryConfig;
import ru.borshchevskiy.pcs.service.ServiceConfig;
import ru.borshchevskiy.pcs.web.WebConfig;

@SpringBootApplication(scanBasePackages = "ru.borshchevskiy.pcs")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
