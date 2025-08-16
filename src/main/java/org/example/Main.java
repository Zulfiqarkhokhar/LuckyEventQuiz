package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.db.DatabaseManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // --- Load Question Screen directly ---
        Parent questionRoot = FXMLLoader.load(getClass().getResource("/fxml/QuestionViewScreen.fxml"));
        Scene questionScene = new Scene(questionRoot, 1280, 720);
        primaryStage.setTitle("Lucky Event - Questions");
        primaryStage.setScene(questionScene);
        primaryStage.show();
        primaryStage.setFullScreen(true);
    }

    public static void main(String[] args) {
        // Initialize database first
        DatabaseManager.initializeDatabase();

        // Optional: verify table creation
        DatabaseManager.checkTablesExist();
        System.out.println("Database setup complete!");

        launch(args);
    }
}
