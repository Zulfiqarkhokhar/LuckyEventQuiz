package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.text.TextFlow;   // ✅ correct package
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class UploadImageModalController {

    @FXML private StackPane overlay;
    @FXML private VBox      card;          // clickable area

    private Consumer<File> onFileSelected = f -> {};

    /* ---------- PUBLIC FACTORY ---------- */
    public static void open(Window owner, Consumer<File> cb) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    UploadImageModalController.class.getResource("/fxml/UploadImageModal.fxml"));
            StackPane modalRoot = fx.load();
            UploadImageModalController ctrl = fx.getController();
            ctrl.onFileSelected = cb;
            ctrl.postInit();

            Parent root = owner.getScene().getRoot();
            StackPane stack = (root instanceof StackPane) ? (StackPane) root
                    : new StackPane(root);
            if (!(root instanceof StackPane)) owner.getScene().setRoot(stack);
            stack.getChildren().add(modalRoot);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* ---------- after FXML loaded ---------- */
    private void postInit() {
        /* keep card fixed‑size */
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(card, Pos.CENTER);
        installDragAndDrop();

        overlay.setOnMouseClicked(e -> {
            /* If the click target *is* the overlay (i.e. outside the card), close */
            if (e.getTarget() == overlay) {
                ((StackPane) overlay.getParent()).getChildren().remove(overlay);
            }
        });
    }


    /* ---------- click handler wired in FXML ---------- */
    @FXML
    private void handleChooseFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Image");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images",
                        "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(overlay.getScene().getWindow());
        if (file != null) finish(file);
    }

    /* ---------- drag‑and‑drop ---------- */
    private void installDragAndDrop() {
        overlay.setOnDragOver(e -> {
            if (e.getGestureSource() != overlay && e.getDragboard().hasFiles())
                e.acceptTransferModes(TransferMode.COPY);
            e.consume();
        });
        overlay.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasFiles()) {
                finish(db.getFiles().get(0));
                e.setDropCompleted(true);
            } else e.setDropCompleted(false);
            e.consume();
        });
    }

    private void finish(File file) {
        onFileSelected.accept(file);
        ((StackPane) overlay.getParent()).getChildren().remove(overlay);
    }
}
