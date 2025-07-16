package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicViewController {

    /* ─────────── FXML refs ─────────── */
    @FXML private FlowPane  musicFlow;
    @FXML private ImageView addIcon;
    @FXML private Label     maxLabel;

    /* ─────────── Config ─────────── */
    private static final int    MAX_AUDIO   = 10;
    private static final double GAP         = 50;
    private static final double CARD_MIN_W  = 400;

    private final List<Node> cardNodes = new ArrayList<>();

    /* ═════════════════ INITIALISE ═════════════════ */
    @FXML
    private void initialize() {
        for (int i = 0; i < 6; i++) addCard("Music 01", "fgsoundvg.mp3", null);

        musicFlow.widthProperty().addListener(
                (o, ov, nv) -> resizeCards(nv.doubleValue()));

        updateAddAvailability();
    }

    /* ═════════════════ Responsive sizing ═════════════════ */
    private void resizeCards(double availableW) {
        int cols = availableW > 900 ? 2 : 1;
        double totalGap = GAP * (cols - 1);
        double width = (availableW - totalGap) / cols;
        if (width < CARD_MIN_W) { cols = 1; width = availableW; }

        musicFlow.setPrefWrapLength(cols * width + totalGap);

        /* plain loop (no lambda) → width is not captured */
        for (Node n : cardNodes) {
            ((Region) n).setPrefWidth(width);
        }
    }

    /* ═════════════════ Card factory ═════════════════ */
    private MusicCardController addCard(String title, String fileName, File source) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    getClass().getResource("/fxml/MusicCard.fxml"));
            final Node cardNode = fx.load();
            final MusicCardController ctrl = fx.getController();

            ctrl.setTitle(title);
            ctrl.setFileName(fileName);

            ctrl.setDeleteCallback(() -> {
                musicFlow.getChildren().remove(cardNode);
                cardNodes.remove(cardNode);
                updateAddAvailability();
            });
            ctrl.setUploadCallback(this::openUploadModal);

            if (source != null && source.exists()) {
                ctrl.setAudioFile(source);
            }

            musicFlow.getChildren().add(cardNode);
            cardNodes.add(cardNode);
            resizeCards(musicFlow.getWidth());

            return ctrl;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load MusicCard.fxml", ex);
        }
    }

    /* ═════════════════ Top Add button ═════════════════ */
    @FXML
    private void handleAddMusic() {
        if (cardNodes.size() >= MAX_AUDIO) return;
        openUploadModal(null);
    }

    /* ═════════════════ Upload modal ═════════════════ */
    private void openUploadModal(MusicCardController target) {
        UploadAudioModalController.open(
                musicFlow.getScene().getWindow(),
                file -> {
                    if (file == null) return;

                    if (target == null) {
                        MusicCardController c =
                                addCard("New Music", file.getName(), file);
                        c.setAudioFile(file);
                    } else {
                        target.setFileName(file.getName());
                        target.setAudioFile(file);
                    }
                    updateAddAvailability();
                });
    }

    /* ═════════════════ Footer stubs ═════════════════ */
    @FXML private void resetMusic() {
        musicFlow.getChildren().clear();
        cardNodes.clear();
        for (int i = 0; i < 6; i++) addCard("Music 01", "fgsoundvg.mp3", null);
        updateAddAvailability();
    }
    @FXML private void saveMusic() {
        System.out.println("Save musics… (TODO persist)");
    }

    /* ═════════════════ Utils ═════════════════ */
    private void updateAddAvailability() {
        boolean maxed = cardNodes.size() >= MAX_AUDIO;
        addIcon.setOpacity(maxed ? 0.3 : 1.0);
        maxLabel.setVisible(maxed);
    }
}
