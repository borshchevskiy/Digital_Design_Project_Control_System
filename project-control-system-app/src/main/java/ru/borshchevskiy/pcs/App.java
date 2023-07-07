package ru.borshchevskiy.pcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages =
        {"ru.borshchevskiy.pcs.entities",
        "ru.borshchevskiy.pcs.service",
        "ru.borshchevskiy.pcs.repository",
        "ru.borshchevskiy.pcs.web",
        "ru.borshchevskiy.pcs.common"})
@EnableAsync
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
