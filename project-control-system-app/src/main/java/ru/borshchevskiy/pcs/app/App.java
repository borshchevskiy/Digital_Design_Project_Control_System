package ru.borshchevskiy.pcs.app;

import ru.borshchevskiy.pcs.controllers.EmployeeController;
import ru.borshchevskiy.pcs.dto.employee.request.EmployeeCreateDto;
import ru.borshchevskiy.pcs.dto.employee.request.EmployeeUpdateDto;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;

public class App {
    public static void main(String[] args) {

        EmployeeController controller = new EmployeeController();

        EmployeeCreateDto ivanDto = new EmployeeCreateDto();
        ivanDto.setFirstname("Ivan");
        ivanDto.setLastname("Ivanov");
        ivanDto.setPatronymic("Ivanovich");
        ivanDto.setPosition("Developer");
        ivanDto.setAccount("Ivan-Developer");
        ivanDto.setEmail("ivan@gmail.com");

        EmployeeCreateDto petrDto = new EmployeeCreateDto();
        petrDto.setFirstname("Petr");
        petrDto.setLastname("Petrov");
        petrDto.setPatronymic("Petrovich");
        petrDto.setPosition("Tester");
        petrDto.setAccount("Petr-Tester");
        petrDto.setEmail("petr@gmail.com");

        EmployeeUpdateDto petrUpdateDto = new EmployeeUpdateDto(2L,
                "Petr",
                "Kuznetsov",
                "Petrovich",
                "Tester",
                "Petr-Tester",
                "petr@gmail.com",
                EmployeeStatus.ACTIVE);

        System.out.println("Saving objects");
        System.out.println(controller.create(ivanDto));//Save Ivan
        System.out.println(controller.create(petrDto));//Save Petr
        System.out.println();

        System.out.println("Get objects");
        System.out.println(controller.getById(2L));//Get Petr
        System.out.println(controller.getById(1L));//Get Ivan
        System.out.println();

        System.out.println("Get all objects");
        System.out.println(controller.getAll());
        System.out.println();

        // При удалении только меняется статус на DELETED, физически не удаляется
        System.out.println("Delete id = 1");
        System.out.println("Employee deleted: " + controller.deleteById(1L));
        System.out.println(controller.getById(1L));
        System.out.println();

        System.out.println("Update id = 2: change lastname to Kuznetsov");
        System.out.println(controller.update(petrUpdateDto));
        System.out.println();

        System.out.println("Try to update DELETED employee with id = 1");
        petrUpdateDto.setId(1L);
        System.out.println(controller.update(petrUpdateDto));
        System.out.println();
    }
}
