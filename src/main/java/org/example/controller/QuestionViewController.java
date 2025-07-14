package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class QuestionViewController {

    @FXML private VBox questionContainer;
    @FXML private ImageView addIcon;
    @FXML private Label      maxLabel;

    /** Max number of questions allowed on screen */
    private static final int MAX_QUESTIONS = 5;

    /** Dummy data to demonstrate the layout */
    private final List<String> sampleQuestions = List.of(
            "This is the question number one which is editable when you click on it?",
            "This is the question number two which has a border when you hover over it?",
            "This is the question number three?",
            "This is the question number four?",
            "This is the question number five?"
    );

    @FXML
    public void initialize() {
        sampleQuestions.forEach(this::addQuestionRow);
        updateAddAvailability();
    }

    /* ------------------------------------------------------------------ */
    /* Event handlers                                                     */
    /* ------------------------------------------------------------------ */

    @FXML
    private void handleAddQuestion() {
        if (questionContainer.getChildren().size() >= MAX_QUESTIONS) return;

        addQuestionRow("New question …");
        updateAddAvailability();
    }

    @FXML
    private void resetQuestions() {
        questionContainer.getChildren().clear();
        sampleQuestions.forEach(this::addQuestionRow);
        updateAddAvailability();
    }

    @FXML
    private void saveQuestions() {
        // TODO: persist changes
        System.out.println("Save clicked — implement persistence here.");
    }

    /* ------------------------------------------------------------------ */
    /* Helpers                                                            */
    /* ------------------------------------------------------------------ */

    private void addQuestionRow(String questionText) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/QuestionRow.fxml"));
            Node row = loader.load();

            QuestionRowController rowCtrl = loader.getController();
            rowCtrl.setQuestionText(questionText);
            rowCtrl.setDeleteCallback(() -> {
                questionContainer.getChildren().remove(row);
                updateAddAvailability();
            });

            questionContainer.getChildren().add(row);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load QuestionRow", e);
        }
    }

    private void updateAddAvailability() {
        boolean maxed = questionContainer.getChildren().size() >= MAX_QUESTIONS;
        addIcon.setOpacity(maxed ? 0.3 : 1.0);
        maxLabel.setVisible(maxed);
    }
}
