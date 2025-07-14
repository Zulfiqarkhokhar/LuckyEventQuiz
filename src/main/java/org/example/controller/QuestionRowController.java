package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * One question row in the scroll list.
 */
public class QuestionRowController {

    @FXML private Label questionLbl;

    private Runnable onDelete = () -> {};
    private Runnable onEdit   = () -> {};

    public void setDeleteCallback(Runnable r) { onDelete = r; }
    public void setEditCallback  (Runnable r) { onEdit   = r; }

    public void setQuestionText(String text) { questionLbl.setText(text); }
    public String getQuestionText()          { return questionLbl.getText(); }

    /* Answers not stored per row in this demo */
    public java.util.List<String> getAnswers() { return java.util.List.of(); }
    public void setAnswers(java.util.List<String> ignored) { /* no-op */ }

    @FXML private void handleDelete() { onDelete.run(); }
    @FXML private void handleEdit()   { onEdit.run();   }
}
