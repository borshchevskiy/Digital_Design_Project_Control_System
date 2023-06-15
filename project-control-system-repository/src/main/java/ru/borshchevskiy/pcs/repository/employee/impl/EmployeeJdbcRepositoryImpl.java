package ru.borshchevskiy.pcs.repository.employee.impl;

import ru.borshchevskiy.pcs.common.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.dto.employee.filter.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.employee.EmployeeJdbcRepository;
import ru.borshchevskiy.pcs.repository.util.jdbc.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;


public class EmployeeJdbcRepositoryImpl implements EmployeeJdbcRepository {

    //    Поиск осуществляется по атрибутам Фамилия, Имя, Отчество, учетной записи, адресу электронной почты.
    private static final List<String> EMPLOYEE_SEARCH_ATTRIBUTES = List.of("firstname", "lastname", "patronymic", "account", "email");

    private static final String FIND_ALL_SQL = """
            SELECT employees.id,
                employees.firstname,
                employees.lastname,
                employees.patronymic,
                employees.position,
                employees.account,
                employees.email,
                employees.status
            FROM employees
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE employees.id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM employees
            WHERE id = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO employees (firstname, lastname, patronymic, position, account, email, status)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String UPDATE_SQL = """
            UPDATE employees
            SET firstname = ?,
                lastname = ?,
                patronymic = ?,
                position = ?,
                account = ?,
                email = ?,
                status = ?
            WHERE id = ?
            """;

    @Override
    public Employee create(Employee employee) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, employee.getFirstname());
            preparedStatement.setString(2, employee.getLastname());
            preparedStatement.setString(3, employee.getPatronymic());
            preparedStatement.setString(4, employee.getPosition());
            preparedStatement.setString(5, String.valueOf(employee.getAccount().getId()));
            preparedStatement.setString(6, employee.getEmail());
            preparedStatement.setString(7, employee.getStatus().name());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                employee.setId(generatedKeys.getLong("id"));
            }

            return employee;

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    @Override
    public Employee update(Employee employee) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, employee.getFirstname());
            preparedStatement.setString(2, employee.getLastname());
            preparedStatement.setString(3, employee.getPatronymic());
            preparedStatement.setString(4, employee.getPosition());
            preparedStatement.setString(5, String.valueOf(employee.getAccount()));
            preparedStatement.setString(6, employee.getEmail());
            preparedStatement.setString(7, employee.getStatus().name());
            preparedStatement.setLong(8, employee.getId());

            preparedStatement.executeUpdate();

            return employee;

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    @Override
    public Optional<Employee> findById(long id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            Employee employee = null;

            if (resultSet.next()) {
                employee = buildEmployee(resultSet);
            }

            return Optional.ofNullable(employee);

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    @Override
    public List<Employee> findAll() {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Employee> employees = new ArrayList<>();

            while (resultSet.next()) {
                employees.add(buildEmployee(resultSet));
            }

            return employees;
        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    /*
    Поиск сотрудников.
    Поиск осуществляется по текстовому значению, которое проверяется по атрибутам Фамилия, Имя, Отчество,
    учетной записи, адресу электронной почты и только среди активных сотрудников.
     */
    @Override
    public List<Employee> findByFilter(EmployeeFilter filter) {
        String searchValue = filter.value();

        if (searchValue.isBlank()) {
            return findAll();
        }

        String where = "WHERE status = 'ACTIVE' AND " + EMPLOYEE_SEARCH_ATTRIBUTES.stream()
                .collect(joining(" LIKE '%" + searchValue + "%' OR ", "(", " LIKE '%" + searchValue + "%')"));

        String sql = FIND_ALL_SQL + where;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Employee> employees = new ArrayList<>();

            while (resultSet.next()) {
                employees.add(buildEmployee(resultSet));
            }

            return employees;

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    private Employee buildEmployee(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();

        employee.setId(resultSet.getLong("id"));
        employee.setFirstname(resultSet.getString("firstname"));
        employee.setLastname(resultSet.getString("lastname"));
        employee.setPatronymic(resultSet.getString("patronymic"));
        employee.setPosition(resultSet.getString("position"));
        employee.setAccount(((Account) resultSet.getObject("account")));
        employee.setEmail(resultSet.getString("email"));
        employee.setStatus(EmployeeStatus.valueOf(resultSet.getString("status").toUpperCase()));

        return employee;
    }
}