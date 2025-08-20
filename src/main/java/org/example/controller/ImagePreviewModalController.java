package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class ImagePreviewModalController {

    @FXML private StackPane overlay;
    @FXML private VBox card;
    @FXML private ImageView previewImage;

    /**
     * Show modal over existing root (like upload modals).
     */
    public static void open(Window owner, Image img){
        try{
            FXMLLoader fx = new FXMLLoader(
                    ImagePreviewModalController.class.getResource("/fxml/ImagePreviewModal.fxml"));
            StackPane modal = fx.load();
            ImagePreviewModalController c = fx.getController();
            c.previewImage.setImage(img);
            c.init();

            Parent root = owner.getScene().getRoot();
            StackPane stack = (root instanceof StackPane) ? (StackPane) root : new StackPane(root);
            if (!(root instanceof StackPane)) owner.getScene().setRoot(stack);
            stack.getChildren().add(modal);

        }catch(IOException e){ e.printStackTrace(); }
    }

    private void init(){
        // ensure card sizes do not stretch overlay background
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    /** clicking outside card closes preview */
    @FXML private void handleBackgroundClick(javafx.scene.input.MouseEvent e){
        if(e.getTarget() == overlay) {
            ((StackPane)overlay.getParent()).getChildren().remove(overlay);
        }
    }

    /** if clicking on the card itself — do not close */
    @FXML private void consumeClick(javafx.scene.input.MouseEvent e){
        e.consume();
    }
}
