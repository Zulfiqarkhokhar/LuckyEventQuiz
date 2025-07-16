package org.example.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

public class MusicCardController {

    /* ─────────── static icons ─────────── */
    private static final Image PLAY_ICON  = icon("icon-plus.png");
    private static final Image PAUSE_ICON = icon("delete-icon.png");
    private static Image icon(String n){
        return new Image(Objects.requireNonNull(
                MusicCardController.class.getResource("/icons/" + n),
                "Missing /icons/" + n).toExternalForm());
    }

    /* ─────────── FXML ─────────── */
    @FXML private VBox    playerPane;
    @FXML private ImageView playIcon;
    @FXML private Slider  slider;
    @FXML private Label   titleLbl, fileLbl, currentLbl, totalLbl;

    /* ─────────── injected callbacks ─────────── */
    private Runnable deleteCb = ()->{};
    private Consumer<MusicCardController> uploadCb = c->{};

    /* ─────────── media state ─────────── */
    private File        audioFile;
    private MediaPlayer player;
    private final BooleanProperty dragging = new SimpleBooleanProperty(false);

    /* ═════════════════ PUBLIC API ═════════════════ */
    public void setTitle(String t){ titleLbl.setText(t); }

    public void setFileName(String n){
        fileLbl.setText(n);
        fileLbl.setTextOverrun(OverrunStyle.ELLIPSIS);
    }

    public void setDeleteCallback(Runnable r){ deleteCb = r; }
    public void setUploadCallback(Consumer<MusicCardController> c){ uploadCb = c; }

    /** called by parent when a file is selected */
    public void setAudioFile(File f){
        stopDispose();
        audioFile = f;
        if(f!=null) loadMedia(f);
    }

    /* ═════════════════ Initialization ═════════════════ */
    @FXML
    private void initialize(){
        slider.setOnMousePressed(e -> dragging.set(true));
        slider.setOnMouseReleased(e -> {
            if(player!=null)
                player.seek(Duration.seconds(slider.getValue()));
            dragging.set(false);
        });
    }

    /* ═════════════════ FXML HANDLERS ═════════════════ */
    @FXML private void delete(){ stopDispose(); deleteCb.run(); }
    @FXML private void upload(){ uploadCb.accept(this); }

    @FXML
    private void togglePlay() {
        if (player == null) {
            System.out.println("No audio loaded.");
            return;
        }

        boolean isPlaying = player.getStatus() == MediaPlayer.Status.PLAYING;

        if (isPlaying) {
            player.pause();
            playIcon.setImage(PLAY_ICON);
            playerPane.setVisible(false);
            playerPane.setManaged(false);
        } else {
            player.play();
            playIcon.setImage(PAUSE_ICON);
            playerPane.setVisible(true);
            playerPane.setManaged(true);
        }
    }


    /* ═════════════════ MediaPlayer setup ═════════════════ */
    private void loadMedia(File f){
        Media media = new Media(f.toURI().toString());
        player = new MediaPlayer(media);

        player.setOnReady(() -> {
            double max = media.getDuration().toSeconds();
            slider.setMax(max);
            totalLbl.setText(fmt(max));
            playIcon.setImage(PLAY_ICON);
            playerPane.setVisible(false);     // collapsed until play
            playerPane.setManaged(false);
        });

        player.currentTimeProperty().addListener((o,ov,nv)->{
            if(!dragging.get()) slider.setValue(nv.toSeconds());
            currentLbl.setText(fmt(nv.toSeconds()));
        });

        player.setOnEndOfMedia(() -> {
            playIcon.setImage(PLAY_ICON);
            playerPane.setVisible(false);
            playerPane.setManaged(false);
        });

    }

    private void stopDispose(){
        if(player!=null){
            player.stop(); player.dispose(); player=null;
        }
    }

    private static String fmt(double s){
        int m=(int)s/60, sec=(int)s%60;
        return String.format("%d:%02d", m, sec);
    }
}
