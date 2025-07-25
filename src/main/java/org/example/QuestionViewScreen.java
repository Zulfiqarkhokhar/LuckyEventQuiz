package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuestionViewScreen {
    public static void show(Stage stage) {
        try {
            Parent root = FXMLLoader.load(QuestionScreen.class.getResource("/fxml/QuestionViewScreen.fxml"));
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(QuestionScreen.class.getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Question Screen");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
