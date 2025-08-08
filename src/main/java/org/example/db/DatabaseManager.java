package org.example.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The DatabaseManager class is responsible for managing the connection to the database.
 * It provides methods to establish a connection and initialize the database schema.
 */
public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:quiz_game.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Initializes the database by creating tables if they don't exist
     */
    public static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "score INTEGER DEFAULT 0)";

        String createQuestionsTable = "CREATE TABLE IF NOT EXISTS questions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question_text TEXT, " +
                "image_path TEXT, " +
                "audio_path TEXT, " +
                "correct_answer TEXT)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Execute table creation
            stmt.execute(createUsersTable);
            stmt.execute(createQuestionsTable);

            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Checks if tables exist in the database
     */
    public static void checkTablesExist() {
        try (Connection conn = connect()) {
            DatabaseMetaData meta = conn.getMetaData();

            // Check users table
            try (ResultSet rs = meta.getTables(null, null, "users", null)) {
                System.out.println("Users table exists: " + rs.next());
            }

            // Check questions table
            try (ResultSet rs = meta.getTables(null, null, "questions", null)) {
                System.out.println("Questions table exists: " + rs.next());
            }
        } catch (SQLException e) {
            System.out.println("Error checking tables: " + e.getMessage());
        }
    }
}