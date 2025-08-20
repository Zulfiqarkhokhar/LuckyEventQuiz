package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.db.DatabaseManager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageGameAdminController {
    @FXML private Button correctBtn;
    @FXML private Button wrongBtn;
    @FXML private Button unlockBtn;
    @FXML private Button nextBtn;

    @FXML private ImageView questionImage;
    @FXML private Label imageDescription;
    @FXML private ProgressBar progressBar;
    @FXML private VBox imageContainer;
    @FXML private VBox answerCard;
    @FXML private VBox mainContainer;

    private Integer currentImageId = null;
    private List<String> imagePaths = new ArrayList<>();
    private List<String> imageTitles = new ArrayList<>();

    @FXML
    public void initialize() {
        // Set up button actions
        correctBtn.setOnAction(e -> {
            System.out.println("Correct answer selected.");
            handleCorrectAnswer();
        });

        wrongBtn.setOnAction(e -> {
            System.out.println("Wrong answer selected.");
            handleWrongAnswer();
        });

        unlockBtn.setOnAction(e -> {
            System.out.println("Buzzers unlocked.");
            handleUnlockBuzzers();
        });

        nextBtn.setOnAction(e -> {
            System.out.println("Next question triggered.");
            loadRandomImage();
        });

        // Load initial images from database
        loadImagePathsFromDatabase();

        // Load the first random image
        if (!imagePaths.isEmpty()) {
            loadRandomImage();
        } else {
            imageDescription.setText("No images found in database. Please add images first.");
        }
    }

    private void loadImagePathsFromDatabase() {
        imagePaths.clear();
        imageTitles.clear();

        try (Connection con = DatabaseManager.connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT file_path, title FROM images")) {

            while (rs.next()) {
                imagePaths.add(rs.getString("file_path"));
                imageTitles.add(rs.getString("title"));
            }

            System.out.println("Loaded " + imagePaths.size() + " images from database");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading images from database: " + e.getMessage());
        }
    }

    private void loadRandomImage() {
        if (imagePaths.isEmpty()) {
            imageDescription.setText("No images available. Please add images first.");
            questionImage.setImage(null);
            return;
        }

        // Get random index
        Random random = new Random();
        int randomIndex;

        // If we have multiple images, avoid showing the same one twice in a row
        if (imagePaths.size() > 1 && currentImageId != null) {
            do {
                randomIndex = random.nextInt(imagePaths.size());
            } while (randomIndex == currentImageId);
        } else {
            randomIndex = random.nextInt(imagePaths.size());
        }

        currentImageId = randomIndex;

        String imagePath = imagePaths.get(randomIndex);
        String imageTitle = imageTitles.get(randomIndex);

        File file = new File(imagePath);
        if (!file.exists()) {
            imageDescription.setText("Image file not found: " + file.getName());
            questionImage.setImage(null);
        } else {
            try {
                Image image = new Image(file.toURI().toString(), true);
                questionImage.setImage(image);
                imageDescription.setText(imageTitle);

                // Reset progress bar
                progressBar.setProgress(0.0);

                System.out.println("Displaying image: " + imageTitle);

            } catch (Exception e) {
                imageDescription.setText("Error loading image: " + e.getMessage());
                questionImage.setImage(null);
            }
        }
    }

    private void handleCorrectAnswer() {
        System.out.println("Correct answer selected.");
        // Update progress bar to show correct answer
        progressBar.setProgress(1.0);
        // Add logic for correct answer (update scores, etc.)
    }

    private void handleWrongAnswer() {
        System.out.println("Wrong answer selected.");
        // Update progress bar to show wrong answer (partial progress)
        progressBar.setProgress(0.3);
        // Add logic for wrong answer
    }

    private void handleUnlockBuzzers() {
        System.out.println("Buzzers unlocked.");
        // Reset progress bar when unlocking buzzers
        progressBar.setProgress(0.0);
        // Add logic to unlock buzzers
    }

    // Optional: Method to refresh the image list from database
    public void refreshImageList() {
        loadImagePathsFromDatabase();
        if (!imagePaths.isEmpty()) {
            loadRandomImage();
        }
    }
}