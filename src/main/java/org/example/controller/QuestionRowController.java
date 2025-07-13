package org.example.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.function.Consumer;

/**
 * Controller for a single question “card” row.
 */
public class QuestionRowController {

    /* -- FXML-injected nodes -- */
    @FXML private Label  questionLabel;
    @FXML private Button deleteBtn;
    @FXML private Button editBtn;

    /* -- Callbacks wired from the parent list -- */
    private Consumer<QuestionRowController> onDelete;
    private Consumer<QuestionRowController> onEdit;

    /* ---------- FXML life-cycle ---------- */
    @FXML
    private void initialize() {
        //  onAction handlers are already declared in FXML, so nothing is
        //  strictly required here – but you COULD add row-level setup if needed.
    }

    /* ---------- Handlers wired in FXML ---------- */
    @FXML
    private void onDelete(ActionEvent e) {
        if (onDelete != null) onDelete.accept(this);
    }

    @FXML
    private void onEdit(ActionEvent e) {
        if (onEdit != null) onEdit.accept(this);
    }

    /* ---------- Public API for the parent controller ---------- */
    public void setQuestionText(String text) { questionLabel.setText(text); }

    public void setOnDelete(Consumer<QuestionRowController> cb) { this.onDelete = cb; }

    public void setOnEdit(Consumer<QuestionRowController> cb)   { this.onEdit   = cb; }
}
