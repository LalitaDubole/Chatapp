package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection.
 * Only ONE connection object exists for the whole app.
 *
 * HOW JDBC WORKS:
 *  1. Load MySQL driver class into JVM memory
 *  2. DriverManager opens a TCP socket to MySQL on port 3306
 *  3. Returns a Connection object — all SQL goes through it
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/chatapp";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";

    private static Connection connection = null;

    private DBConnection() { }   // nobody can call new DBConnection()

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);  // load driver
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connected to MySQL.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] Driver not found! Add mysql-connector-j.jar to lib/");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DB] Cannot connect! Check username/password and MySQL service.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}