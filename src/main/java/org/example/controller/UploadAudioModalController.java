package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

public class UploadAudioModalController {

    @FXML private StackPane overlay;
    @FXML private VBox      card;

    private Consumer<File> callback = f->{};

    public static void open(Window owner, Consumer<File> cb){
        try{
            FXMLLoader fx=new FXMLLoader(
                    UploadAudioModalController.class.getResource("/fxml/UploadAudioModal.fxml"));
            StackPane modal=fx.load();
            UploadAudioModalController c=fx.getController();
            c.callback=cb; c.initDrag();
            Parent root=owner.getScene().getRoot();
            StackPane stack=root instanceof StackPane?(StackPane)root:new StackPane(root);
            if(!(root instanceof StackPane)) owner.getScene().setRoot(stack);
            stack.getChildren().add(modal);
        }catch(IOException e){throw new RuntimeException(e);}
    }

    @FXML private void chooseFile(){
        FileChooser fc=new FileChooser();
        fc.setTitle("Select Audio");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio",
                        "*.mp3","*.aac","*.wav","*.flac","*.ogg"));
        File f=fc.showOpenDialog(overlay.getScene().getWindow());
        if(f!=null) finish(f);
    }

    private void initDrag(){
        card.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        StackPane.setAlignment(card, Pos.CENTER);

        overlay.setOnDragOver(e->{
            if(e.getDragboard().hasFiles()) e.acceptTransferModes(TransferMode.COPY);
            e.consume();
        });
        overlay.setOnDragDropped(e->{
            Dragboard db=e.getDragboard();
            finish(db.getFiles().get(0));
            e.setDropCompleted(true); e.consume();
        });
        overlay.setOnMouseClicked(e->{
            if(e.getTarget()==overlay) close();
        });
    }
    private void finish(File f){ callback.accept(f); close();}
    private void close(){ ((StackPane)overlay.getParent()).getChildren().remove(overlay); }
}
