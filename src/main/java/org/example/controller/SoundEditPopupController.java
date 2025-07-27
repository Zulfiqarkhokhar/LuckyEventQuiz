package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundEditPopupController {

    @FXML
    private Label currentSoundLabel;

    @FXML
    private Slider timeSlider;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Button playButton;

    private File selectedSoundFile;
    private MediaPlayer mediaPlayer;

    private Runnable onClose; // 👈 Callback just like EditPlayerPopupController

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    @FXML
    private void onChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Sound File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.ogg", "*.flac", "*.aac")
        );
        File file = fileChooser.showOpenDialog(null); // if using as embedded popup, pass null
        if (file != null) {
            selectedSoundFile = file;
            currentSoundLabel.setText(file.getName());

            try {
                if (mediaPlayer != null) {
                    mediaPlayer.dispose();
                }
                Media media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnReady(() -> {
                    durationLabel.setText(formatTime(media.getDuration().toSeconds()));
                    timeSlider.setMax(media.getDuration().toSeconds());
                });

                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    timeSlider.setValue(newTime.toSeconds());
                    currentTimeLabel.setText(formatTime(newTime.toSeconds()));
                });

            } catch (Exception e) {
                showAlert("Failed to load audio file.");
            }
        }
    }

    @FXML
    private void onPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        } else {
            showAlert("No sound file selected.");
        }
    }

    @FXML
    private void onSave() {
        if (selectedSoundFile == null) {
            showAlert("Please select a sound file before saving.");
            return;
        }

        System.out.println("Saving selected sound file: " + selectedSoundFile.getAbsolutePath());

        closePopup(); // 👈 This will now call the callback
    }

    @FXML
    private void onCancel() {
        closePopup(); // 👈 Consistent cancel
    }

    private void closePopup() {
        if (onClose != null) {
            onClose.run();  // ✅ Let parent controller handle hiding
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String formatTime(double seconds) {
        int totalSecs = (int) seconds;
        int mins = totalSecs / 60;
        int secs = totalSecs % 60;
        return String.format("%d:%02d", mins, secs);
    }
}
