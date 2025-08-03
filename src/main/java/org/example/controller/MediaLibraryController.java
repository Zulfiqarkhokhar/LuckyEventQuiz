package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

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

        cardQuestions.setOnMouseClicked(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/QuestionViewScreen.fxml"));
                Stage stage = (Stage) cardQuestions.getScene().getWindow();
                Scene scene = new Scene(root);

                scene.getStylesheets().add(getClass().getResource("/fxml/question-view.css").toExternalForm());
                stage.setScene(scene);
                stage.setFullScreen(true);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        cardMusic.setOnMouseClicked(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/MusicViewScreen.fxml"));
                Stage stage = (Stage) cardMusic.getScene().getWindow();
                Scene scene = new Scene(root);

                scene.getStylesheets().add(getClass().getResource("/css/music.css").toExternalForm());
                stage.setScene(scene);
                stage.setFullScreen(true);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        cardImages.setOnMouseClicked(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/ImageViewScreen.fxml"));
                Stage stage = (Stage) cardImages.getScene().getWindow();
                Scene scene = new Scene(root);

                scene.getStylesheets().add(getClass().getResource("/css/images.css").toExternalForm());
                stage.setScene(scene);
                stage.setFullScreen(true);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
