package org.example.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class QuestionController {

    @FXML private Label questionLabel;
    @FXML private GridPane cardGrid;     // ✅ correct type

    @FXML
    private void initialize() {
        // Responsive font: scale question size between 24 px and 64 px
        Scene scene = questionLabel.getScene();
        if (scene != null) {
            bindFont(scene);
        } else {
            // Scene isn't available at init → wait until layout pass
            questionLabel.sceneProperty().addListener((obs, oldScene, newScene) -> bindFont(newScene));
        }
    }

    private void bindFont(Scene scene) {
        questionLabel.styleProperty().bind(
                Bindings.format("-fx-font-size: %.0fpx;",
                        scene.widthProperty().divide(32)) // tweak divisor for taste
        );
    }
}
