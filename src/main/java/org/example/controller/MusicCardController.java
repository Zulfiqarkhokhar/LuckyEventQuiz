package org.example.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

    private int id;
    public void setId(int id) { this.id=id; }
    public int getId() { return id; }

    private static final Image PLAY_ICON  = icon("Play.png");
    private static final Image PAUSE_ICON = icon("Pause.png");
    private static Image icon(String n){
        return new Image(Objects.requireNonNull(
                MusicCardController.class.getResource("/icons/" + n),
                "Missing /icons/" + n).toExternalForm());
    }

    @FXML private VBox playerPane;
    @FXML private ImageView playIcon, uploadIcon;
    @FXML private Slider slider;
    @FXML private Label titleLbl, fileLbl, currentLbl, totalLbl;

    private Runnable deleteCb = ()->{};
    private Consumer<MusicCardController> uploadCb = c->{};

    private File audioFile;
    private MediaPlayer player;
    private final BooleanProperty dragging = new SimpleBooleanProperty(false);

    public void setTitle(String t){ titleLbl.setText(t); }
    public void setFileName(String n){
        fileLbl.setText(n);
        fileLbl.setTextOverrun(OverrunStyle.ELLIPSIS);
    }
    public void setDeleteCallback(Runnable r){ deleteCb = r; }
    public void setUploadCallback(Consumer<MusicCardController> c){ uploadCb = c; }

    public void setAudioFile(File f){
        stopDispose();
        audioFile = f;
        if(f!=null) loadMedia(f);
    }

    @FXML
    private void initialize(){
        slider.setOnMousePressed(e -> dragging.set(true));
        slider.setOnMouseReleased(e -> {
            if(player!=null) player.seek(Duration.seconds(slider.getValue()));
            dragging.set(false);
        });

        // apply styles once the control is in scene graph
        slider.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                styleSlider();
            }
        });
    }


    @FXML private void delete(){ stopDispose(); deleteCb.run(); }
    @FXML private void upload(){ uploadCb.accept(this); }

    @FXML private void togglePlay(){
        if (player == null) {
            return;
        }

        boolean isPlaying = player.getStatus() == MediaPlayer.Status.PLAYING;

        if (isPlaying) {
            player.pause();
            playIcon.setImage(PLAY_ICON);
            uploadIcon.setVisible(false);
            uploadIcon.setManaged(false);
            collapse();
        } else {
            // reset if at end
            if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {
                player.seek(Duration.ZERO);
            }
            player.play();
            playIcon.setImage(PAUSE_ICON);
            uploadIcon.setVisible(true);
            uploadIcon.setManaged(true);
            expand();
        }
    }


    private void loadMedia(File f){
        Media media = new Media(f.toURI().toString());
        player = new MediaPlayer(media);
        player.setOnReady(() -> {
            double max = media.getDuration().toSeconds();
            slider.setMax(max);
            totalLbl.setText(fmt(max));
            playerPane.setVisible(false);
            playerPane.setManaged(false);
            playIcon.setImage(PLAY_ICON);
            forceSliderColors();  // <— add this line
        });
        player.currentTimeProperty().addListener((o,ov,nv)->{
            if(!dragging.get()) slider.setValue(nv.toSeconds());
            currentLbl.setText(fmt(nv.toSeconds()));
        });
        player.setOnEndOfMedia(() -> {
            playIcon.setImage(PLAY_ICON);
            collapse();
        });
    }

    private void stopDispose(){
        if(player!=null){ player.stop(); player.dispose(); player=null; }
    }
    private static String fmt(double s){
        int m=(int)s/60, sec=(int)s%60;
        return String.format("%d:%02d",m,sec);
    }
    private void styleSlider() {
        // Unplayed track
        Node track = slider.lookup(".track");
        if(track != null){
            track.setStyle("-fx-background-color: #cccccc; -fx-pref-height: 3px;");
        }

        // Played-filled portion
        Node fill = slider.lookup(".filled-track");
        if(fill != null){
            fill.setStyle("-fx-background-color: #000000;");
        }

        // Thumb
        Node thumb = slider.lookup(".thumb");
        if(thumb != null){
            thumb.setStyle("-fx-background-color: #000000; -fx-background-radius: 5px; -fx-pref-width:10px; -fx-pref-height:10px;");
        }
    }
    private void forceSliderColors() {
        slider.applyCss(); // force skin to generate

        Node track = slider.lookup(".track");
        if (track != null) {
            track.setStyle("-fx-background-color: #cccccc; -fx-pref-height: 3px;");
        }

        Node fill = slider.lookup(".track > *"); // covers filled or colored-track
        if (fill != null) {
            fill.setStyle("-fx-background-color: #000000;");
        }

        Node thumb = slider.lookup(".thumb");
        if (thumb != null) {
            thumb.setStyle("-fx-background-color: #000000; -fx-background-radius: 5px; -fx-pref-width:10; -fx-pref-height:10;");
        }
    }


    private void collapse(){ playerPane.setVisible(false); playerPane.setManaged(false); }
    private void expand(){   playerPane.setVisible(true);  playerPane.setManaged(true);  }
}
