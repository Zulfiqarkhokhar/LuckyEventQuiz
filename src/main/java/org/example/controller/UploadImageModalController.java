package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/** Modal that lets user choose / drag an image, then returns File. */
public class UploadImageModalController {

    @FXML private StackPane overlay;
    @FXML private Label     textLabel;

    private Consumer<File> onFileSelected = f -> {};

    /* ―――― Public API ―――― */
    public static void open(Window owner, Consumer<File> callback) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    UploadImageModalController.class.getResource("/fxml/UploadImageModal.fxml"));
            StackPane modalRoot = fx.load();
            UploadImageModalController ctrl = fx.getController();
            ctrl.onFileSelected = callback;
            ctrl.installDragAndDrop();

            /* attach to root StackPane (create wrapper if needed) */
            Parent sceneRoot = owner.getScene().getRoot();
            StackPane stackRoot = (sceneRoot instanceof StackPane)
                    ? (StackPane) sceneRoot
                    : new StackPane(sceneRoot);

            if (!(sceneRoot instanceof StackPane))
                owner.getScene().setRoot(stackRoot);

            stackRoot.getChildren().add(modalRoot);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* ―――― click selects file ―――― */
    @FXML
    private void handleMouseClicked() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Image");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(overlay.getScene().getWindow());
        if (file != null) finish(file);
    }

    /* ―――― drag & drop support ―――― */
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
