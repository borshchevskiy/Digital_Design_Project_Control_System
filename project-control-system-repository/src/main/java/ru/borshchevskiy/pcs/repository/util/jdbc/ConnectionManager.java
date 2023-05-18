package ru.borshchevskiy.pcs.repository.util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String PASSWORD_KEY = "db.password";
    private static final String USERNAME_KEY = "db.username";
    private static final String URL_KEY = "db.url";

    static {
        loadDriver();
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    ConnectionPropertiesUtil.get(URL_KEY),
                    ConnectionPropertiesUtil.get(USERNAME_KEY),
                    ConnectionPropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Database access error!", e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC driver was not found!", e);
        }
    }

}
