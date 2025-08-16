package org.example.db;

import java.sql.*;

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
     * Initializes DB with users, questions, and question_options tables
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
                "audio_path TEXT" +
                ")";

        String createOptionsTable = "CREATE TABLE IF NOT EXISTS question_options (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question_id INTEGER NOT NULL, " +
                "option_text TEXT NOT NULL, " +
                "is_correct INTEGER DEFAULT 0, " +
                "FOREIGN KEY(question_id) REFERENCES questions(id)" +
                ")";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createQuestionsTable);
            stmt.execute(createOptionsTable);

            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Checks if DB tables exist
     */
    public static void checkTablesExist() {
        try (Connection conn = connect()) {
            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet rs = meta.getTables(null, null, "users", null)) {
                System.out.println("Users table exists: " + rs.next());
            }
            try (ResultSet rs = meta.getTables(null, null, "questions", null)) {
                System.out.println("Questions table exists: " + rs.next());
            }
            try (ResultSet rs = meta.getTables(null, null, "question_options", null)) {
                System.out.println("QuestionOptions table exists: " + rs.next());
            }

        } catch (SQLException e) {
            System.out.println("Error checking tables: " + e.getMessage());
        }
    }
}
