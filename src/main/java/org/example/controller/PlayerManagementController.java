package org.example.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class PlayerManagementController {

    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane editPlayerPopup;
    @FXML private AnchorPane editSoundPopup;
    @FXML private TextField soundNameField;
    @FXML private Label soundFileLabel;
    private File selectedSoundFile;

    @FXML
    private void onEditPlayer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditPlayerPopup.fxml"));
            Parent popupContent = loader.load();

            EditPlayerPopupController controller = loader.getController();
            controller.setOnClose(this::hidePlayerPopup);

            // Clear the container and add the new content
            editPlayerPopup.getChildren().setAll(popupContent);

            // Center the content within the container
            AnchorPane.setTopAnchor(popupContent, 0.0);
            AnchorPane.setBottomAnchor(popupContent, 0.0);
            AnchorPane.setLeftAnchor(popupContent, 0.0);
            AnchorPane.setRightAnchor(popupContent, 0.0);

            editPlayerPopup.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), editPlayerPopup);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hidePlayerPopup() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), editPlayerPopup);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> editPlayerPopup.setVisible(false));
        fadeOut.play();
    }


    private void hideSoundPopup() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), editSoundPopup);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> editSoundPopup.setVisible(false));
        fadeOut.play();
    }


    @FXML
    public void onEditSound() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SoundEditPopup.fxml"));
            Parent popupContent = loader.load();

            SoundEditPopupController controller = loader.getController();
            controller.setOnClose(this::hideSoundPopup); // ✅ Set the callback

            editSoundPopup.getChildren().setAll(popupContent);

            // Center the content within the container
            AnchorPane.setTopAnchor(popupContent, 0.0);
            AnchorPane.setBottomAnchor(popupContent, 0.0);
            AnchorPane.setLeftAnchor(popupContent, 0.0);
            AnchorPane.setRightAnchor(popupContent, 0.0);

            editSoundPopup.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), editSoundPopup);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load sound editor.");
        }
    }



    @FXML
    private void onBrowseSoundFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Sound File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.ogg")
        );
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            selectedSoundFile = file;
            soundFileLabel.setText(file.getName());
        }
    }

    @FXML
    private void onSaveSound() {
        String name = soundNameField.getText();
        if (name == null || name.isBlank()) {
            showAlert("Sound name is required.");
            return;
        }

        if (selectedSoundFile == null) {
            showAlert("Please select a sound file.");
            return;
        }

        System.out.println("Saving sound: " + name + ", file: " + selectedSoundFile.getAbsolutePath());

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), editSoundPopup);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> editSoundPopup.setVisible(false));
        fadeOut.play();
    }

    @FXML
    private void onCancelSound() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), editSoundPopup);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> editSoundPopup.setVisible(false));
        fadeOut.play();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }
}
