package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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
        currentGameLabel.setText("Lucky Wheel Round 1");
        playersLabel.setText("09");
        statusLabel.setText("READY");
    }
}
