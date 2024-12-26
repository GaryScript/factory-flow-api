package be.alb_mar_hen.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class FactoryFlowConnection {
    private static Connection instance = null;

    private FactoryFlowConnection() {
        try {
            Properties properties = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("databaseconfig.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find databaseconfig.properties");
                    return;
                }
                properties.load(input);
            }
            
            String driver = properties.getProperty("db.driver");
            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            Class.forName(driver);
            
            instance = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException | SQLException | IOException ex) {
            System.out.println("Database connection error: " + ex.getMessage());
            System.exit(0);  
        }

        if (instance == null) {
            System.out.println("The database isn't accessible, program closing.");
            System.exit(0);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new FactoryFlowConnection();
        }
        return instance;
    }

    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                instance = null;
            }
        } catch (SQLException ex) {
            System.out.println("Error closing the connection: " + ex.getMessage());
        }
    }   
}
