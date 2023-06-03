package ru.borshchevskiy.pcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.borshchevskiy.pcs")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
