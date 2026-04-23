import java.sql.*;

class Database {
    public static Connection connection;

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/online_store?serverTimezone=America/New_York",
                    "root",
                    "h@ppyAbhi#007"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void autoCloseDB() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Database.connection.close();
                System.out.println("Application Closed - DB Connection Closed");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, "Shutdown-thread"));
    }
}