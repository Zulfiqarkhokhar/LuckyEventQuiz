package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Images grid screen.
 * • 3 → 2 → 1 responsive columns centred in view
 * • "Add Image" opens UploadImageModal (click or drag‑and‑drop)
 * • each card supports delete / upload / preview (stub)
 */
public class ImagesViewController {

    /* ────────── FXML refs ────────── */
    @FXML private FlowPane  imageFlow;
    @FXML private ImageView addIcon;
    @FXML private Label     maxLabel;

    /* ────────── Config ────────── */
    private static final int    MAX_IMAGES     = 6;    // can raise later
    private static final double H_GAP          = 60;   // must equal imageFlow hgap
    private static final double TARGET_CARD_W  = 340;  // desktop size
    private static final double MIN_CARD_W     = 280;  // never narrower
    private static final String SAMPLE_TITLE   = "Image 01";

    /* Track card nodes for resize / delete */
    private final List<Node> cardNodes = new ArrayList<>();

    /* ═════════════════ INITIALISE ═════════════════ */
    @FXML
    private void initialize() {
        /* 6 sample cards */
        for (int i = 0; i < 6; i++) addImageCard(SAMPLE_TITLE);

        /* responsive listener */
        imageFlow.widthProperty().addListener(
                (obs, oldV, newV) -> updateResponsiveLayout(newV.doubleValue()));

        updateAddAvailability();
    }

    /* ═════════════════ Responsive layout ═════════════════ */
    private void updateResponsiveLayout(double availableW) {
        if (availableW <= 0) return;

        /* 1. pick starting columns based on TARGET width, clamp 1..3 */
        int columns = Math.max(1,
                Math.min(3,
                        (int) ((availableW + H_GAP) / (TARGET_CARD_W + H_GAP))));

        /* 2. ensure width not < MIN; drop columns if necessary */
        while (columns > 1) {
            double candidate = (availableW - (columns - 1) * H_GAP) / columns;
            if (candidate >= MIN_CARD_W) break;
            columns--;
        }

        /* 3. integer card width prevents jitter */
        int cardW = (int) Math.floor(
                (availableW - (columns - 1) * H_GAP) / columns);

        /* 4. centre rows by setting FlowPane wrap length */
        double rowW = columns * cardW + (columns - 1) * H_GAP;
        imageFlow.setPrefWrapLength(rowW);

        /* 5. apply width only if changed > 0.5 px */
        for (Node n : cardNodes) {
            Region r = (Region) n;
            if (Math.abs(r.getPrefWidth() - cardW) > 0.5) r.setPrefWidth(cardW);
        }
    }

    /* ═════════════ Card factory helpers ═════════════ */

    /** Low‑level: create card, wire callbacks, add to grid; returns controller. */
    private ImageCardController makeCard(String title) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    getClass().getResource("/fxml/ImageCard.fxml"));
            Node cardNode = fx.load();
            ImageCardController ctrl = fx.getController();

            ctrl.setTitle(title);

            /* callbacks */
            ctrl.setDeleteCallback(() -> {
                imageFlow.getChildren().remove(cardNode);
                cardNodes.remove(cardNode);
                updateAddAvailability();
            });
            ctrl.setUploadCallback(this::chooseAndLoadImage);
            ctrl.setExpandCallback(this::showPreview);

            imageFlow.getChildren().add(cardNode);
            cardNodes.add(cardNode);

            /* first sizing */
            updateResponsiveLayout(imageFlow.getWidth());
            return ctrl;

        } catch (IOException ex) {
            throw new RuntimeException("Cannot load ImageCard.fxml", ex);
        }
    }

    /** Blank card. */
    private void addImageCard(String title) {
        makeCard(title);
    }

    /** Card with pre‑selected file. */
    private void addImageCard(String title, File file) {
        ImageCardController ctrl = makeCard(title);
        if (file != null && file.exists()) {
            ctrl.setImage(new Image(file.toURI().toString(), true));
            ctrl.setTitle(file.getName());
        }
    }

    /* ═════════════ Top‑right “Add Image” ═════════════ */
    @FXML
    private void handleAddImage() {
        if (cardNodes.size() >= MAX_IMAGES) return;
        addImageCard("New Image");       // blank card, no modal
        updateAddAvailability();
    }


    /* ═════════════ Upload icon inside a card ═════════════ */
    /* Upload icon inside a card ----------------------------------------- */
    private void chooseAndLoadImage(ImageCardController card) {
        /* Use drag‑&‑drop modal */
        UploadImageModalController.open(
                imageFlow.getScene().getWindow(),
                file -> {
                    if (file != null) {
                        card.setImage(new Image(file.toURI().toString(), true));
                        card.setTitle(file.getName());
                    }
                });
    }


    /* ═════════════ Preview icon (stub) ═════════════ */
    private void showPreview(ImageCardController card) {
        System.out.println("Preview for " + card);
        // TODO: implement lightbox preview
    }

    /* ═════════════ Footer buttons ═════════════ */
    @FXML private void resetImages() {
        imageFlow.getChildren().clear();
        cardNodes.clear();
        for (int i = 0; i < 6; i++) addImageCard(SAMPLE_TITLE);
        updateAddAvailability();
    }
    @FXML private void saveImages() {
        System.out.println("Save Images clicked (TODO persist).");
    }

    /* ═════════════ Utility ═════════════ */
    private void updateAddAvailability() {
        boolean maxed = cardNodes.size() >= MAX_IMAGES;
        addIcon.setOpacity(maxed ? 0.3 : 1.0);
        maxLabel.setVisible(maxed);
    }
}
