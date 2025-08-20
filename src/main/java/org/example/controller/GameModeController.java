package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.util.GameState;

import java.io.IOException;

public class GameModeController {

    @FXML private Button startQuestionBtn;
    @FXML private Button startImagesBtn;
    @FXML private Button startMusicBtn;
    @FXML private Button selectAllInOneBtn;
    @FXML private Button selectSpontaneousBtn;

    @FXML
    public void initialize() {
        startQuestionBtn.setText("Select Question Game");
        startImagesBtn.setText("Select Images Game");
        startMusicBtn.setText("Select Music Game");
        selectAllInOneBtn.setText("Select All-in-One Game");
        selectSpontaneousBtn.setText("Select Spontaneous Game");

        startQuestionBtn.setOnAction(e -> selectGame("Question Game"));
        startImagesBtn.setOnAction(e -> selectGame("Images Game"));
        startMusicBtn.setOnAction(e -> selectGame("Music Game"));
        selectAllInOneBtn.setOnAction(e -> selectGame("All-in-One Game"));
        selectSpontaneousBtn.setOnAction(e -> selectGame("Spontaneous Game"));
    }

    private void selectGame(String gameName) {
        try {
            GameState.setCurrentGame(gameName);

            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/fxml/DashboardScreen.fxml"));
            Stage stage = (Stage) startQuestionBtn.getScene().getWindow();
            Scene scene = new Scene(dashboardRoot, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setFullScreen(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
