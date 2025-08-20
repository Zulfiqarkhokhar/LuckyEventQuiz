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
     * Initializes DB with users, questions, options and musics table.
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

        String createMusicTable = "CREATE TABLE IF NOT EXISTS musics (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "file_name TEXT NOT NULL, " +
                "file_path TEXT NOT NULL" +
                ")";

        String createImagesTable = "CREATE TABLE IF NOT EXISTS images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "file_name TEXT NOT NULL, " +
                "file_path TEXT NOT NULL" +
                ")";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createQuestionsTable);
            stmt.execute(createOptionsTable);
            stmt.execute(createMusicTable);
            stmt.execute(createImagesTable);

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
            try (ResultSet rs = meta.getTables(null, null, "musics", null)) {
                System.out.println("Musics table exists: " + rs.next());
            }
            try (ResultSet rs = meta.getTables(null, null, "images", null)) {
                System.out.println("Images table exists: " + rs.next());
            }

        } catch (SQLException e) {
            System.out.println("Error checking tables: " + e.getMessage());
        }
    }
}
