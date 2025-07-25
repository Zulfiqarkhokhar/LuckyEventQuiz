package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Small pill in the edit dialog */
public class AnswerChip {

    @FXML private Label textLabel;
    private Runnable onDelete;

    void init(String text, Runnable onDelete) {
        textLabel.setText(text);
        this.onDelete = onDelete;
    }

    @FXML
    private void handleDelete() {
        if (onDelete != null) onDelete.run();
    }

    String getText() { return textLabel.getText(); }
}
