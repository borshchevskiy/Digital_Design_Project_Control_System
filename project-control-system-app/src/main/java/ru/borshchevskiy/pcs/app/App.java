package ru.borshchevskiy.pcs.app;

import ru.borshchevskiy.pcs.controllers.EmployeeController;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.repository.task.impl.TaskJdbcRepositoryImpl;

public class App {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("hi");
        System.out.println("Waiting for database init...");

        Thread.sleep(3000);

        /*
        Во время инициализации выполнена вставка тестовых данных в employees

        INSERT INTO employees (firstname, lastname, patronymic, position, account, email, status)
        VALUES ('Ivan', 'Ivanov', 'Ivanovich', 'Developer', 'ivan-developer', 'ivan@gmail.com', 'ACTIVE'),
       ('Petr', 'Petrov', 'Petrovich', 'Tester', 'petr-tester', 'petr@gmail.com', 'ACTIVE'),
       ('Anton', 'Antonov', 'Antonovich', 'Manager', 'anton-developer', 'anton@gmail.com', 'ACTIVE');
         */

        EmployeeController controller = new EmployeeController();

        EmployeeDto semenDto = new EmployeeDto();
        semenDto.setFirstname("Andrey");
        semenDto.setLastname("Andreev");
        semenDto.setPatronymic("Andreevich");
        semenDto.setPosition("Devops");
        semenDto.setAccount("andrey-devops");
        semenDto.setEmail("andrey@gmail.com");

        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setId(1L);
        updateDto.setFirstname("Vladimir");
        updateDto.setLastname("Vladimirov");
        updateDto.setPatronymic("Vladimirovich");
        updateDto.setPosition("Analyst");
        updateDto.setAccount("vladimir-analyst");
        updateDto.setEmail("vladimir@gmail.com");
        updateDto.setStatus(EmployeeStatus.ACTIVE);


        System.out.println();
        System.out.println("Get all employees");
        System.out.println(controller.getAll());
        System.out.println();
        System.out.println("Get employee with id=1");
        System.out.println(controller.getById(1L));
        System.out.println();
        System.out.println("Update employee name with id=1");
        System.out.println(controller.update(updateDto));
        System.out.println();
        System.out.println("Save Andrey");
        System.out.println(controller.create(semenDto));
        System.out.println();
        System.out.println("Search for gmail");
        System.out.println(controller.getByFilter(new EmployeeFilter("gmail")));
        System.out.println();
        System.out.println("Search for andrey");
        System.out.println(controller.getByFilter(new EmployeeFilter("andrey")));
        System.out.println();

        System.out.println("-----------------------------------------------------------------");

        /*
        Во время инициализации выполнена вставка тестовых данных в employees

        INSERT INTO tasks (name, description, implementer_id, labor_costs, deadline, status, author_id, date_created,
                   date_updated, project_id)
        VALUES
        ('API development', 'Develop API functionality', 1, 100, '2023-12-31', 'IN_WORK', 3, '2023-05-01', null,  1),
       ('Repository development', 'Develop repository functionality', 1, 100, '2023-12-31', 'IN_WORK', 3,'2023-05-01', null, 1),
       ('API testing', 'Test API', 2, 100, '2023-12-31', 'IN_WORK', 3, '2023-05-01', null, 2),
       ('Security testing', 'Test security', 2, 100, '2023-12-31', 'IN_WORK', 3, '2023-05-01', null, 2);
         */

        System.out.println("Search all tasks");
        TaskJdbcRepositoryImpl taskJdbcRepository = new TaskJdbcRepositoryImpl();
        for (Task task : taskJdbcRepository.getAll()) {
            System.out.println(task);
            System.out.println();
        }

        System.out.println("Search tasks by filter");
        System.out.println("Search tasks 1 and 3 - name contains 'API'");
        TaskFilter filter1 = new TaskFilter("API", null, null, null, null, null);
        // Должен найти таски 1 и 3
        for (Task task : taskJdbcRepository.findByFilter(filter1)) {
            System.out.println(task);
            System.out.println();
        }
        System.out.println("Search task 1 - name contains 'API' and implementer lastname is Petrov (JOIN with employees)");
        // Должен найти таску 3
        TaskFilter filter2 = new TaskFilter("API", null, "Petrov", null, null, null);
        System.out.println(taskJdbcRepository.findByFilter(filter2));
    }
}
