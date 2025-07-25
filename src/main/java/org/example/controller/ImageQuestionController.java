package org.example.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/** Controls ImageQuestion.fxml */
public class ImageQuestionController {

    @FXML private BorderPane root;          // whole scene graph
    @FXML private Label      questionLabel;
    @FXML private ImageView  questionImage;

    @FXML
    private void initialize() {
        // 1. Load a dummy picture (can be replaced later)
        questionImage.setImage(new Image("https://picsum.photos/1200/800", true));

        // 2. Keep image height ≤ 65 % of the window, preserving aspect ratio
        questionImage.fitHeightProperty().bind(
                Bindings.createDoubleBinding(() ->
                                root.getHeight() * 0.6,            // 65 %
                        root.heightProperty()));
    }

    /* ---------- Public setters so the game logic can update content ---------- */

    public void setQuestionText(String text) { questionLabel.setText(text); }

    public void setImage(String url) {
        questionImage.setImage(new Image(url, true));   // async load
    }
}
