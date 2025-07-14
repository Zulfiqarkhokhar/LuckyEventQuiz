package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

import java.util.function.Consumer;

public class QuestionRowController {

    @FXML private Label questionLbl;

    /** Callback set by parent controller for delete action */
    private Runnable onDelete = () -> {};
    public void setDeleteCallback(Runnable r) { this.onDelete = r; }

    /* ------------------------------------------------------------------ */
    /* Public API                                                         */
    /* ------------------------------------------------------------------ */

    public void setQuestionText(String text) {
        questionLbl.setText(text);
    }

    /* ------------------------------------------------------------------ */
    /* Event handlers                                                     */
    /* ------------------------------------------------------------------ */

    @FXML
    private void handleDelete() {
        onDelete.run();
    }

    @FXML
    private void handleEdit() {
        TextInputDialog dlg = new TextInputDialog(questionLbl.getText());
        dlg.setHeaderText("Edit Question");
        dlg.setContentText("Question:");
        dlg.showAndWait().ifPresent(questionLbl::setText);
    }
}
