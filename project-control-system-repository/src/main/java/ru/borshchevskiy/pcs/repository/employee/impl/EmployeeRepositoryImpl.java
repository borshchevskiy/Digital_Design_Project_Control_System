package ru.borshchevskiy.pcs.repository.employee.impl;

import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    private static final File STORAGE_PATH;

    // Этот обхект генерирует айдишники для сущностей
    private final AtomicLong idGenerator;

    // Создаем папку-хранилище
    static {
        STORAGE_PATH = new File("storage", "employees");
        STORAGE_PATH.mkdirs();
    }

    public EmployeeRepositoryImpl() {
        // Инициализируем генератор айдишников, стартовое значение равно максимальному айдишнику в хранилище, либо 0
        long startValue = Arrays.stream(Objects.requireNonNull(STORAGE_PATH.list()))
                .mapToLong(Long::valueOf)
                .max()
                .orElse(0L);
        idGenerator = new AtomicLong(startValue);


    }

    @Override
    public Employee create(Employee employee) {
        employee.setId(idGenerator.incrementAndGet());

        File file = new File(STORAGE_PATH, String.valueOf(employee.getId()));

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(employee);

        } catch (IOException e) {
            System.out.println("Error initializing stream");
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        File file = new File(STORAGE_PATH, String.valueOf(employee.getId()));

        // Полностью перезаписывает файл с объектом
        try (OutputStream os = Files.newOutputStream(file.toPath(), StandardOpenOption.TRUNCATE_EXISTING);
             ObjectOutputStream oos = new ObjectOutputStream(os)) {

            oos.writeObject(employee);

        } catch (IOException e) {
            System.out.println("Error initializing stream");
            e.printStackTrace();
        }

        return employee;
    }

    @Override
    public Optional<Employee> getById(long id) {
        File file = new File(STORAGE_PATH, String.valueOf(id));
        Optional<Employee> optionalEmployee = Optional.empty();

        try (FileInputStream fos = new FileInputStream(file);
             ObjectInputStream oos = new ObjectInputStream(fos)) {

            optionalEmployee = Optional.ofNullable((Employee) oos.readObject());

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            e.printStackTrace();
        }

        return optionalEmployee;
    }

    @Override
    public List<Employee> getAll() {
        File[] files = STORAGE_PATH.listFiles();
        if (files == null) {
            throw new RuntimeException("Storage is null!");
        }

        List<Employee> employees = new ArrayList<>();

        for (File file : files) {
            try (FileInputStream fos = new FileInputStream(file);
                 ObjectInputStream oos = new ObjectInputStream(fos)) {

                employees.add((Employee) oos.readObject());

            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                System.out.println("Error initializing stream");
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                e.printStackTrace();
            }
        }

        return employees;
    }


    /*
    Этот метод физически удаляет файл
     */
    @Override
    public void deleteById(Long id) {
        File file = new File(STORAGE_PATH, String.valueOf(id));
        if (file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                System.out.println("Error initializing stream");
            }
        } else {
            System.out.println("Employee doesn't exist!");
        }
    }

    /*
    Этот метод меняет статус сотрудника на DELETED
     */
    @Override
    public void delete(Employee employee) {
        update(employee);
    }
}
