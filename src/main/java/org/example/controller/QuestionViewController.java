package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

/**
 * Main Questions screen controller.
 */
public class QuestionViewController {

    @FXML private VBox      questionContainer;
    @FXML private ImageView addIcon;
    @FXML private Label     maxLabel;

    private static final int MAX_QUESTIONS = 5;

    private final List<String> sample = List.of(
            "This is the question number one which is editable when you click on it?",
            "This is the question number two which has a border when you hover over it?",
            "This is the question number three?",
            "This is the question number four?",
            "This is the question number five?"
    );

    @FXML
    public void initialize() {
        sample.forEach(this::addQuestionRow);
        updateAddAvailability();
    }

    /* ───────── create and insert a row ───────── */
    private void addQuestionRow(String text) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    getClass().getResource("/fxml/QuestionRow.fxml"));
            Node rowNode = fx.load();
            QuestionRowController row = fx.getController();

            row.setQuestionText(text);

            row.setDeleteCallback(() -> {
                questionContainer.getChildren().remove(rowNode);
                updateAddAvailability();
            });

            row.setEditCallback(() -> openEditModal(row));

            questionContainer.getChildren().add(rowNode);

        } catch (IOException ex) {
            throw new RuntimeException("Load row failed", ex);
        }
    }

    /* ─────────────── add / reset / save ─────────────── */
    @FXML private void handleAddQuestion() { openAddModal(); }

    @FXML
    private void resetQuestions() {
        questionContainer.getChildren().clear();
        sample.forEach(this::addQuestionRow);
        updateAddAvailability();
    }

    @FXML
    private void saveQuestions() {
        // TODO: persist to backend
        System.out.println("Save clicked (stub).");
    }

    /* ──────────── modal helpers ──────────── */
    private void openAddModal() {
        QuestionModalController.open(
                getWindow(),
                null,
                null,
                (q, a) -> {
                    if (!q.isBlank()) {
                        addQuestionRow(q);
                        updateAddAvailability();
                    }
                });
    }

    private void openEditModal(QuestionRowController row) {
        QuestionModalController.open(
                getWindow(),
                row.getQuestionText(),
                row.getAnswers(),
                (q, a) -> row.setQuestionText(q));
    }

    /* ───────────── util ───────────── */
    private void updateAddAvailability() {
        boolean maxed = questionContainer.getChildren().size() >= MAX_QUESTIONS;
        addIcon.setOpacity(maxed ? 0.3 : 1.0);
        maxLabel.setVisible(maxed);
    }

    private Window getWindow() {
        return questionContainer.getScene().getWindow();
    }
}
