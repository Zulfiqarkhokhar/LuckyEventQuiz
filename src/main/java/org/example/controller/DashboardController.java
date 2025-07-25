package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

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
    }
}
