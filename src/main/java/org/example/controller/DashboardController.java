package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button playerBtn;

    @FXML
    private Button mediaBtn;

    @FXML
    private Button brandingBtn;

    @FXML
    private Button gameModeBtn;

    @FXML
    private Button startGameBtn;

    @FXML
    private Label currentGameLabel;

    @FXML
    private Label playersLabel;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        currentGameLabel.setText("CURRENT GAME");
        playersLabel.setText("PLAYERS");
        statusLabel.setText("STATUS");

        // 🔁 Game Mode Navigation (new addition)
        gameModeBtn.setOnAction(e -> {
            try {
                Parent gameModeRoot = FXMLLoader.load(getClass().getResource("/fxml/GameModeSelector.fxml"));
                Stage stage = (Stage) gameModeBtn.getScene().getWindow();
                stage.getScene().setRoot(gameModeRoot);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        playerBtn.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/PlayerManagement.fxml"));
                Stage stage = (Stage) playerBtn.getScene().getWindow();
                Scene scene = new Scene(root);

                scene.getStylesheets().add(getClass().getResource("/css/player-management.css").toExternalForm());
                stage.setScene(scene);
                stage.setFullScreen(true);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        brandingBtn.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/BrandingSettings.fxml"));
                Stage stage = (Stage) brandingBtn.getScene().getWindow();
                Scene scene = new Scene(root);

                scene.getStylesheets().add(getClass().getResource("/css/branding-settings.css").toExternalForm());
                stage.setScene(scene);
                stage.setFullScreen(true);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
