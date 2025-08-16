package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * One question row in the scroll list.
 */

public class QuestionRowController {

    @FXML private Label questionLbl;

    private Runnable onDelete = () -> {};
    private Runnable onEdit   = () -> {};

    private final List<String> answers = new ArrayList<>();

    public void setDeleteCallback(Runnable r) { onDelete = r; }
    public void setEditCallback  (Runnable r) { onEdit   = r; }

    public void setQuestionText(String text) {
        questionLbl.setText(text);
    }

    public String getQuestionText() {
        return questionLbl.getText();
    }

    /** called by QuestionViewController after loading row from DB or modal */
    public void setAnswers(List<String> provided) {
        answers.clear();
        if (provided != null) {
            answers.addAll(provided);
        }
    }

    /** used by Edit modal (QuestionModalController.open) */
    public List<String> getAnswers() {
        return new ArrayList<>(answers);
    }

    @FXML private void handleDelete() { onDelete.run(); }
    @FXML private void handleEdit()   { onEdit.run(); }
}
