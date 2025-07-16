package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Auto-scales three media cards and ensures the bottom label bar
 * spans the full card width.
 */
public class MediaLibraryController {

    @FXML private HBox cardBox;
    @FXML private VBox cardQuestions, cardImages, cardMusic;

    @FXML
    private void initialize() {
        cardBox.widthProperty().addListener((obs, oldV, newV) -> {
            double totalGap = cardBox.getSpacing() * 2;            // two gaps
            double widthPer = (newV.doubleValue() - totalGap) / 3; // 3 cards

            for (VBox card : new VBox[]{cardQuestions, cardImages, cardMusic}) {
                /* resize whole card */
                card.setPrefWidth(widthPer);

                /* resize its label (last child) */
                Label lbl = (Label) card.getChildren().get(1);
                lbl.setPrefWidth(widthPer);
            }
        });
    }
}
