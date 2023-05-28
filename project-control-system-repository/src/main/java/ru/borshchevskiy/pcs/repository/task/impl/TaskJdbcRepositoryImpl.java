package ru.borshchevskiy.pcs.repository.task.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.enums.ProjectStatus;
import ru.borshchevskiy.pcs.enums.TaskStatus;
import ru.borshchevskiy.pcs.repository.employee.impl.EmployeeJdbcRepositoryImpl;
import ru.borshchevskiy.pcs.repository.task.TaskJdbcRepository;
import ru.borshchevskiy.pcs.repository.util.jdbc.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@Repository
@RequiredArgsConstructor
public class TaskJdbcRepositoryImpl implements TaskJdbcRepository {

    EmployeeJdbcRepositoryImpl employeeJdbcRepository;

    private static final String FIND_ALL_SQL = """
            SELECT tasks.id,
                tasks.name,
                tasks.description,
                tasks.implementer_id,
                tasks.labor_costs,
                tasks.deadline,
                tasks.status,
                tasks.author_id,
                tasks.date_created,
                tasks.date_updated,
                tasks.project_id,
                ea.id AS author_id,
                ea.firstname AS author_firstname,
                ea.lastname AS author_lastname,
                ea.patronymic AS author_patronymic,
                ea.position AS author_position,
                ea.account AS author_account,
                ea.email AS author_email,
                ea.status AS author_status,
                ei.id AS implementer_id,
                ei.firstname AS implementer_firstname,
                ei.lastname AS implementer_lastname,
                ei.patronymic AS implementer_patronymic,
                ei.position AS implementer_position,
                ei.account AS implementer_account,
                ei.email AS implementer_email,
                ei.status AS implementer_status,
                p.id AS project_id,
                p.code AS project_code,
                p.name AS project_name,
                p.description AS project_description,
                p.status AS project_status
            FROM tasks
            JOIN employees ea on ea.id = tasks.author_id
            JOIN employees ei on ei.id = tasks.implementer_id
            JOIN projects p on p.id = tasks.project_id
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE tasks.id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM tasks
            WHERE id = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO tasks (name, description, implementer_id, labor_costs, deadline, status, author_id, date_created, date_updated, project_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String UPDATE_SQL = """
            UPDATE tasks
            SET name = ?,
                description = ?,
                implementer_id = ?,
                labor_costs = ?,
                deadline = ?,
                status = ?,
                author_id = ?,
                date_created = ?,
                date_updated = ?,
                project_id = ?
            WHERE id = ?
            """;

    @Override
    public Task create(Task task) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setLong(3, task.getImplementer().getId());
            preparedStatement.setInt(4, task.getLaborCosts());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(task.getDeadline()));
            preparedStatement.setString(6, task.getStatus().name());
            preparedStatement.setLong(7, task.getAuthor().getId());
            preparedStatement.setTimestamp(8, Timestamp.valueOf(task.getDateCreated()));
            preparedStatement.setTimestamp(9, Timestamp.valueOf(task.getDateUpdated()));
            preparedStatement.setLong(10, task.getProject().getId());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                task.setId(generatedKeys.getLong("id"));
            }

            return task;

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    @Override
    public Task update(Task task) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setLong(3, task.getImplementer().getId());
            preparedStatement.setInt(4, task.getLaborCosts());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(task.getDeadline()));
            preparedStatement.setString(6, task.getStatus().name());
            preparedStatement.setLong(7, task.getAuthor().getId());
            preparedStatement.setTimestamp(8, Timestamp.valueOf(task.getDateCreated()));
            preparedStatement.setTimestamp(9, Timestamp.valueOf(task.getDateUpdated()));
            preparedStatement.setLong(10, task.getProject().getId());
            preparedStatement.setLong(11, task.getId());

            preparedStatement.executeUpdate();

            return task;

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    @Override
    public Optional<Task> getById(long id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            Task task = null;

            if (resultSet.next()) {
                task = buildTask(resultSet);
            }

            return Optional.ofNullable(task);

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    @Override
    public List<Task> getAll() {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Task> tasks = new ArrayList<>();

            while (resultSet.next()) {
                tasks.add(buildTask(resultSet));
            }

            return tasks;
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


    public List<Task> findByFilter(TaskFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();

        if (filter.name() != null) {
            whereSql.add("tasks.name LIKE ?");
            parameters.add("%" + filter.name() + "%");
        }

        if (filter.status() != null) {
            whereSql.add("tasks.status = ?");
            parameters.add(filter.status().name());
        }

        if (filter.implementerName() != null) {
            whereSql.add("ei.lastname LIKE ?");
            parameters.add("%" + filter.implementerName() + "%");
        }

        if (filter.authorName() != null) {
            whereSql.add("ea.lastname LIKE ?");
            parameters.add("%" + filter.authorName() + "%");
        }

        if (filter.deadline() != null) {
            whereSql.add("tasks.deadline = ?");
            parameters.add(filter.deadline());
        }

        if (filter.dateCreated() != null) {
            whereSql.add("tasks.date_created = ?");
            parameters.add(filter.dateCreated());
        }

        String where = whereSql.stream()
                .collect(joining(" AND ", " WHERE ", " ORDER BY tasks.date_created DESC "));

        String sql = FIND_ALL_SQL + where;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Task> tasks = new ArrayList<>();

            while (resultSet.next()) {
                tasks.add(buildTask(resultSet));
            }

            return tasks;

        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    private Task buildTask(ResultSet resultSet) throws SQLException {
        Employee implementer = new Employee();
        implementer.setId(resultSet.getLong("implementer_id"));
        implementer.setFirstname(resultSet.getString("implementer_firstname"));
        implementer.setLastname(resultSet.getString("implementer_lastname"));
        implementer.setPatronymic(resultSet.getString("implementer_patronymic"));
        implementer.setPosition(resultSet.getString("implementer_position"));
        implementer.setAccount((Account) resultSet.getObject("implementer_account"));
        implementer.setEmail(resultSet.getString("implementer_email"));
        implementer.setStatus(EmployeeStatus.valueOf(resultSet.getString("implementer_status").toUpperCase()));

        Employee author = employeeJdbcRepository.findById(resultSet.getLong("author_id")).orElse(null);
//        Employee author = new Employee();
//        author.setId(resultSet.getLong("author_id"));
//        author.setFirstname(resultSet.getString("author_firstname"));
//        author.setLastname(resultSet.getString("author_lastname"));
//        author.setPatronymic(resultSet.getString("author_patronymic"));
//        author.setPosition(resultSet.getString("author_position"));
//        author.setAccount(resultSet.getString("author_account"));
//        author.setEmail(resultSet.getString("author_email"));
//        author.setStatus(EmployeeStatus.valueOf(resultSet.getString("author_status").toUpperCase()));

        Project project = new Project();
        project.setId(resultSet.getLong("project_id"));
        project.setCode(resultSet.getString("project_code"));
        project.setName(resultSet.getString("project_name"));
        project.setDescription(resultSet.getString("project_description"));
        project.setStatus(ProjectStatus.valueOf(resultSet.getString("project_status").toUpperCase()));

        Task task = new Task();
        LocalDateTime updated = null;
        if (resultSet.getTimestamp("date_updated") != null) {
            updated = resultSet.getTimestamp("date_updated").toLocalDateTime();
        }

        task.setId(resultSet.getLong("id"));
        task.setName(resultSet.getString("name"));
        task.setDescription(resultSet.getString("description"));
        task.setImplementer(implementer);
        task.setLaborCosts(resultSet.getInt("labor_costs"));
        task.setDeadline(resultSet.getTimestamp("deadline").toLocalDateTime());
        task.setStatus(TaskStatus.valueOf(resultSet.getString("status").toUpperCase()));
        task.setAuthor(author);
        task.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
        task.setDateUpdated(updated);
        task.setProject(project);

        return task;
    }
}
