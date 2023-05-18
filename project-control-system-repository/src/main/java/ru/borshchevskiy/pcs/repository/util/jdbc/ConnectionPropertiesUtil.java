package ru.borshchevskiy.pcs.repository.util.jdbc;

import ru.borshchevskiy.pcs.repository.employee.impl.EmployeeJdbcRepositoryImpl;

import java.io.IOException;
import java.util.Properties;

public class ConnectionPropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (var inputStream = EmployeeJdbcRepositoryImpl.class.getClassLoader().getResourceAsStream("application-jdbc.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Can't access JDBC properties!", e);
        }
    }
}
