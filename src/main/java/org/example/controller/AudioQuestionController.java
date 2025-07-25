package org.example.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

/** Handles AudioQuestion.fxml */
public class AudioQuestionController {

    @FXML private BorderPane root;        // already injected
    @FXML private Button playPauseButton;
    @FXML private Slider progressSlider;
    @FXML private Label  currentTime;
    @FXML private Label  totalTime;
    @FXML private StackPane audioPanel;  // new
    @FXML private StackPane  loadingOverlay;   // injected overlay



    private MediaPlayer mediaPlayer;
    private boolean userIsDragging = false;

    @FXML
    private void initialize() {

        /* panel width = 75 % of window width, but never wider than 600 px */
        audioPanel.maxWidthProperty().bind(
                Bindings.min(600,          // hard ceiling
                        root.widthProperty().multiply(0.75))
        );
        audioPanel.prefWidthProperty().bind(audioPanel.maxWidthProperty());
        /* 1 ─ split comma-separated styleClass lists once at startup */
        normaliseStyleClasses(root);



        /* 2 ─ button : toggle play / pause, 30 px vs 40 px glyphs */
        playPauseButton.setOnAction(e -> togglePlayPause());
        playPauseButton.setStyle("-fx-font-size: 40;");   // default for ▶

        /* 3 ─ load demo track */
        loadAudio("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
    }

    /* ───────────────────────────────────────── helpers ───────────────────────────────────────── */

    private void togglePlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    private void loadAudio(String url) {
        if (mediaPlayer != null) mediaPlayer.dispose();
        mediaPlayer = new MediaPlayer(new Media(url));

        mediaPlayer.setOnReady(() -> {
            Duration total = mediaPlayer.getTotalDuration();
            totalTime.setText(format(total));
            progressSlider.setMax(total.toSeconds());
        });

        /* keep UI synced */
        mediaPlayer.currentTimeProperty().addListener((obs, o, n) -> {
            if (!userIsDragging) progressSlider.setValue(n.toSeconds());
            currentTime.setText(format(n));
        });

        /* slider drag / click */
        progressSlider.valueChangingProperty().addListener((obs, was, is) -> {
            userIsDragging = is;
            if (!is) mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
        });
        progressSlider.setOnMouseReleased(e ->
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue())));

        /* play/pause icon + per-glyph font size */
        mediaPlayer.statusProperty().addListener((obs, old, status) -> {
            switch (status) {
                case PLAYING -> { playPauseButton.setText("❚❚"); playPauseButton.setStyle("-fx-font-size: 30;"); }
                case PAUSED, STOPPED, READY -> { playPauseButton.setText("▶"); playPauseButton.setStyle("-fx-font-size: 40;"); }
                default -> { }
            }
        });
    }

    private static String format(Duration d) {
        int sec = (int) d.toSeconds();
        return sec / 60 + ":" + String.format("%02d", sec % 60);
    }

    /* ── converts "class1, class2" → ["class1","class2"] so commas work like spaces ── */
    private static void normaliseStyleClasses(Parent root) {
        for (Node n : root.lookupAll("*")) {
            List<String> fixed = new ArrayList<>();
            for (String cls : n.getStyleClass()) {
                if (cls.contains(",")) {
                    for (String part : cls.split(",")) fixed.add(part.trim());
                } else fixed.add(cls);
            }
            n.getStyleClass().setAll(fixed);
        }
    }
}
