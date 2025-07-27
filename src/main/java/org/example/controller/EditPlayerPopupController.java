package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EditPlayerPopupController {

    @FXML
    private Label buzzerIdLabel;

    @FXML
    private ComboBox<String> colorComboBox;

    @FXML
    private Rectangle colorBox;

    private Runnable onClose;

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setBuzzerData(String id, String color) {
        buzzerIdLabel.setText("ID " + id);
        colorComboBox.getSelectionModel().select(color);
        updateColorBox(color);
    }

    @FXML
    public void initialize() {
        colorComboBox.getItems().addAll("Red", "Blue", "Green", "Yellow");
        colorComboBox.getSelectionModel().select("Red");
        updateColorBox("Red");

        colorComboBox.setOnAction(e -> updateColorBox(colorComboBox.getValue()));
    }

    private void updateColorBox(String color) {
        colorBox.setFill(Color.web(color.toLowerCase()));
    }

    @FXML
    private void onCancel() {
        if (onClose != null) onClose.run();
    }

    @FXML
    private void onSave() {
        // Save logic here
        if (onClose != null) onClose.run();
    }
}
