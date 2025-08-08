package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class QuestionGameAdminController {

    @FXML private Button correctBtn;
    @FXML private Button wrongBtn;
    @FXML private Button unlockBtn;
    @FXML private Button nextBtn;

    @FXML
    public void initialize() {
        correctBtn.setOnAction(e -> {
            System.out.println("Correct answer selected.");
            // Logic for correct answer
        });

        wrongBtn.setOnAction(e -> {
            System.out.println("Wrong answer selected.");
            // Logic for wrong answer
        });

        unlockBtn.setOnAction(e -> {
            System.out.println("Buzzers unlocked.");
            // Logic to unlock buzzers
        });

        nextBtn.setOnAction(e -> {
            System.out.println("Next question triggered.");
            // Logic to move to next question
        });
    }
}
