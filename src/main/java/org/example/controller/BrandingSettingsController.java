package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import java.io.File;

public class BrandingSettingsController {

    @FXML
    private Label hexCodeLabel;

    @FXML
    private Label colorBox;

    @FXML
    private Button resetBtn, saveBtn;

    @FXML
    public void initialize() {
        // Simulate color swatch click
        colorBox.setOnMouseClicked(e -> {
            // You could open a ColorPicker or FileChooser here
            // Simulated color update
            String hex = "#EEB431"; // New selected color
            colorBox.setStyle("-fx-background-color: " + hex);
            hexCodeLabel.setText(hex);
        });

        resetBtn.setOnAction(e -> {
            colorBox.setStyle("-fx-background-color: #EEB431;");
            hexCodeLabel.setText("#EEB431");
            // Reset logo preview and text here if needed
        });

        saveBtn.setOnAction(e -> {
            // Handle saving logic
            System.out.println("Settings saved with color: " + hexCodeLabel.getText());
        });
    }
}
