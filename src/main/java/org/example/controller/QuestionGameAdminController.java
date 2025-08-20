package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QuestionGameAdminController {

    @FXML private Button correctBtn;
    @FXML private Button wrongBtn;
    @FXML private Button unlockBtn;
    @FXML private Button nextBtn;

    @FXML private Label questionLabel;
    @FXML private HBox answersContainer;
    @FXML private VBox answerA;
    @FXML private VBox answerB;
    @FXML private VBox answerC;
    @FXML private VBox answerD;
    @FXML private Label answerAText;
    @FXML private Label answerBText;
    @FXML private Label answerCText;
    @FXML private Label answerDText;

    // Store current question ID to avoid showing the same question twice
    private Integer currentQuestionId = null;

    @FXML
    public void initialize() {
        // Set up button actions
        correctBtn.setOnAction(e -> handleCorrectAnswer());
        wrongBtn.setOnAction(e -> handleWrongAnswer());
        unlockBtn.setOnAction(e -> handleUnlockBuzzers());
        nextBtn.setOnAction(e -> loadRandomQuestion());

        // Load the first question
        loadRandomQuestion();
    }

    private void handleCorrectAnswer() {
        System.out.println("Correct answer selected.");
        // Add logic for correct answer (update scores, etc.)
    }

    private void handleWrongAnswer() {
        System.out.println("Wrong answer selected.");
        // Add logic for wrong answer
    }

    private void handleUnlockBuzzers() {
        System.out.println("Buzzers unlocked.");
        // Add logic to unlock buzzers
    }

    private void loadRandomQuestion() {
        try (Connection c = DatabaseManager.connect()) {
            // Get single random question (excluding current one if it exists)
            String query;
            PreparedStatement qs;

            if (currentQuestionId != null) {
                query = "SELECT * FROM questions WHERE id != ? ORDER BY RANDOM() LIMIT 1";
                qs = c.prepareStatement(query);
                qs.setInt(1, currentQuestionId);
            } else {
                query = "SELECT * FROM questions ORDER BY RANDOM() LIMIT 1";
                qs = c.prepareStatement(query);
            }

            ResultSet qrs = qs.executeQuery();

            if (!qrs.next()) {
                System.out.println("No more questions found in database");
                // If no more questions, reset and try again without exclusion
                currentQuestionId = null;
                loadRandomQuestion();
                return;
            }

            currentQuestionId = qrs.getInt("id");
            questionLabel.setText(qrs.getString("question_text"));

            // Get options for this question
            PreparedStatement os = c.prepareStatement(
                    "SELECT option_text, is_correct FROM question_options WHERE question_id=? ORDER BY id");
            os.setInt(1, currentQuestionId);
            ResultSet ors = os.executeQuery();

            List<String> options = new ArrayList<>();
            while (ors.next()) {
                options.add(ors.getString("option_text"));
            }

            // Display options in the answer boxes
            displayOptions(options);

            System.out.println("Loaded question ID: " + currentQuestionId);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading question: " + e.getMessage());
        }
    }

    private void displayOptions(List<String> options) {
        // Hide all answer boxes initially
        answerA.setVisible(false);
        answerB.setVisible(false);
        answerC.setVisible(false);
        answerD.setVisible(false);

        // Show and populate only the needed answer boxes
        if (options.size() > 0) {
            answerA.setVisible(true);
            answerAText.setText(options.get(0));
        }

        if (options.size() > 1) {
            answerB.setVisible(true);
            answerBText.setText(options.get(1));
        }

        if (options.size() > 2) {
            answerC.setVisible(true);
            answerCText.setText(options.get(2));
        }

        if (options.size() > 3) {
            answerD.setVisible(true);
            answerDText.setText(options.get(3));
        }

        // If you have more than 4 options, you might want to handle this case
        if (options.size() > 4) {
            System.out.println("Warning: Question has more than 4 options, only showing first 4");
        }
    }
}