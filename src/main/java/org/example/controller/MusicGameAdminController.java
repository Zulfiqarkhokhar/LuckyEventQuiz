package org.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.db.DatabaseManager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicGameAdminController {
    @FXML private Button correctBtn;
    @FXML private Button wrongBtn;
    @FXML private Button unlockBtn;
    @FXML private Button nextBtn;
    @FXML private Button playPauseBtn;

    @FXML private Label songTitleLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Slider musicSlider;
    @FXML private VBox musicContainer;

    private MediaPlayer mediaPlayer;
    private Timeline timeline;
    private List<String> musicPaths = new ArrayList<>();
    private List<String> musicTitles = new ArrayList<>();
    private Integer currentMusicId = null;
    private boolean isPlaying = false;

    @FXML
    public void initialize() {
        // Set up button actions
        correctBtn.setOnAction(e -> handleCorrectAnswer());
        wrongBtn.setOnAction(e -> handleWrongAnswer());
        unlockBtn.setOnAction(e -> handleUnlockBuzzers());
        nextBtn.setOnAction(e -> loadRandomMusic());
        playPauseBtn.setOnAction(e -> togglePlayPause());

        // Load initial music from database
        loadMusicPathsFromDatabase();

        // Load the first random music
        if (!musicPaths.isEmpty()) {
            loadRandomMusic();
        } else {
            songTitleLabel.setText("No music found in database. Please add music first.");
        }

        // Set up slider behavior
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null && Math.abs(newVal.doubleValue() - oldVal.doubleValue()) > 1) {
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(newVal.doubleValue() / 100.0));
            }
        });
    }

    private void loadMusicPathsFromDatabase() {
        musicPaths.clear();
        musicTitles.clear();

        try (Connection con = DatabaseManager.connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT file_path, title FROM musics")) {

            while (rs.next()) {
                String path = rs.getString("file_path");
                String title = rs.getString("title");

                File file = new File(path);
                if (file.exists()) {
                    musicPaths.add(path);
                    musicTitles.add(title);
                } else {
                    System.out.println("Skipping missing file: " + path);
                }
            }

            System.out.println("Loaded " + musicPaths.size() + " music files from database");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading music from database: " + e.getMessage());
        }
    }

    private void loadRandomMusic() {
        // Stop current playback
        stopPlayback();

        if (musicPaths.isEmpty()) {
            songTitleLabel.setText("No music available. Please add music first.");
            return;
        }

        // Get random index
        Random random = new Random();
        int randomIndex;

        // If we have multiple music files, avoid playing the same one twice in a row
        if (musicPaths.size() > 1 && currentMusicId != null) {
            do {
                randomIndex = random.nextInt(musicPaths.size());
            } while (randomIndex == currentMusicId);
        } else {
            randomIndex = random.nextInt(musicPaths.size());
        }

        currentMusicId = randomIndex;

        String musicPath = musicPaths.get(randomIndex);
        String musicTitle = musicTitles.get(randomIndex);

        try {
            // Create media player
            Media media = new Media(new File(musicPath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // Set up media player event handlers
            mediaPlayer.setOnReady(() -> {
                songTitleLabel.setText(musicTitle);
                totalTimeLabel.setText(formatTime(mediaPlayer.getTotalDuration()));
                setupTimeline();
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                stopPlayback();
                playPauseBtn.setText("▶");
                isPlaying = false;
            });

            System.out.println("Loaded music: " + musicTitle);

        } catch (Exception e) {
            songTitleLabel.setText("Error loading music: " + e.getMessage());
            System.out.println("Error loading music file: " + e.getMessage());
        }
    }

    private void setupTimeline() {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> updateProgress()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateProgress() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration totalTime = mediaPlayer.getTotalDuration();

            if (totalTime.greaterThan(Duration.ZERO)) {
                double progress = currentTime.toMillis() / totalTime.toMillis() * 100;
                musicSlider.setValue(progress);
                currentTimeLabel.setText(formatTime(currentTime));
            }
        }
    }

    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (isPlaying) {
            mediaPlayer.pause();
            playPauseBtn.setText("▶");
        } else {
            mediaPlayer.play();
            playPauseBtn.setText("⏸");
        }
        isPlaying = !isPlaying;
    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        if (timeline != null) {
            timeline.stop();
        }
        isPlaying = false;
        playPauseBtn.setText("▶");
        musicSlider.setValue(0);
        currentTimeLabel.setText("0:00");
    }

    private void handleCorrectAnswer() {
        System.out.println("Correct answer selected.");
        stopPlayback();
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

    // Clean up resources when controller is no longer needed
    public void cleanup() {
        stopPlayback();
    }

    // Optional: Method to refresh the music list from database
    public void refreshMusicList() {
        loadMusicPathsFromDatabase();
        if (!musicPaths.isEmpty()) {
            loadRandomMusic();
        }
    }
}