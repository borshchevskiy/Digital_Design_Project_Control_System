package ru.borshchevskiy.pcs.app;

import ru.borshchevskiy.pcs.controllers.Controller;
import ru.borshchevskiy.pcs.dto.Dto;
import ru.borshchevskiy.pcs.entities.Entity;
import ru.borshchevskiy.pcs.services.Service;
import ru.borshchevskiy.pcs.repository.Repository;

import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello, from Project Control System App!");
        System.out.println();
        String message = "%s from %s created!\r\n";

        Controller controller = new Controller();
        Dto dto = new Dto();
        Service service = new Service();
        Entity entity = new Entity();
        Repository repository = new Repository();

        Stream.of(controller, dto, service, entity, repository)
                .forEach(x -> System.out.printf(message, x.getClass().getSimpleName(), x.getClass().getCanonicalName()));

    }
}
