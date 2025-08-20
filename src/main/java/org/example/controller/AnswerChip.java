package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AnswerChip {

    @FXML private TextField textField;

    private Runnable onDelete = () -> {};
    public void setOnDelete(Runnable r) { this.onDelete = r; }

    public void setText(String text) {
        textField.setText(text);
        // Leave placeholder already set from FXML (promptText="Answer text…")
    }

    public String getText() {
        return textField.getText();
    }

    public TextField getTextField() {
        return textField;
    }

    @FXML
    private void handleDelete() {
        onDelete.run();
    }
}
