package ru.borshchevskiy.pcs.repository.employee.impl;

import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.employee.EmployeeFileRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeeFileRepositoryImpl implements EmployeeFileRepository {

    private static final File STORAGE_PATH;

    // Этот объект генерирует айдишники для сущностей
    private final AtomicLong idGenerator;

    // Создаем папку-хранилище
    static {
        STORAGE_PATH = new File("storage", "employees");
        STORAGE_PATH.mkdirs();
    }

    public EmployeeFileRepositoryImpl() {
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
            return null;
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
            return null;
        }

        return employee;
    }

    @Override
    public Optional<Employee> findById(long id) {
        File file = new File(STORAGE_PATH, String.valueOf(id));
        Optional<Employee> optionalEmployee = Optional.empty();

        try (FileInputStream fos = new FileInputStream(file);
             ObjectInputStream oos = new ObjectInputStream(fos)) {

            optionalEmployee = Optional.ofNullable((Employee) oos.readObject());

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
        }

        return optionalEmployee;
    }

    @Override
    public List<Employee> findAll() {
        File[] files = STORAGE_PATH.listFiles();
        List<Employee> employees = new ArrayList<>();

        if (files == null) {
            return employees;
        }

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

    @Override
    public List<Employee> findByFilter(EmployeeFilter filter) {
        return null;
    }

}
