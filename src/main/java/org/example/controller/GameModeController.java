package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class GameModeController {

    @FXML private Button startQuestionBtn;
    @FXML private Button startImagesBtn;
    @FXML private Button startMusicBtn;

    @FXML
    public void initialize() {
        startQuestionBtn.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/QuestionGameAdminView.fxml"));
                Stage stage = (Stage) startQuestionBtn.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/css/question-game.css").toExternalForm());
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        startImagesBtn.setOnAction(e -> {
            System.out.println("Navigate to Image Game");
        });

        startMusicBtn.setOnAction(e -> {
            System.out.println("Navigate to Music Game");
        });
    }

}
