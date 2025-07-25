package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.io.File;
import java.util.function.Consumer;

/**
 * Controller for one image tile/card.
 */
public class ImageCardController {

    @FXML private VBox      cardRoot;
    @FXML private Label     titleLbl;
    @FXML private ImageView deleteIcon;
    @FXML private ImageView uploadIcon;
    @FXML private StackPane previewWrapper;
    @FXML private ImageView previewImage;
    @FXML private ImageView expandIcon;

    /* callbacks injected by parent */
    private Runnable onDelete = () -> {};
    private Consumer<ImageCardController> onExpand = c -> {};
    private Consumer<ImageCardController> onUpload = c -> {};

    public void setDeleteCallback(Runnable r) { onDelete = r; }
    public void setExpandCallback(Consumer<ImageCardController> c) { onExpand = c; }
    public void setUploadCallback(Consumer<ImageCardController> c) { onUpload = c; }

    public void setTitle(String title) { titleLbl.setText(title); }

    /** Replace preview image (after upload). */
    public void setImage(Image img) { previewImage.setImage(img); }

    /** Convenience for sample images from file path. */
    public void setImageFromFile(File file) {
        if (file != null && file.exists()) {
            previewImage.setImage(new Image(file.toURI().toString(), true));
        }
    }

    /* FXML event handlers */
    @FXML private void handleDelete() { onDelete.run(); }
    @FXML private void handleExpand() { onExpand.accept(this); }
    @FXML private void handleUpload() { onUpload.accept(this); }

    @FXML
    private void initialize() {
        /* make expand icon sit bottom-right inside preview */
        StackPane.setAlignment(expandIcon, Pos.BOTTOM_RIGHT);
    }
}
