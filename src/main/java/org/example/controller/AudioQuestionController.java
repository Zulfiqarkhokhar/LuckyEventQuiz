package org.example.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.db.DatabaseManager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioQuestionController {

    @FXML private BorderPane root;
    @FXML private Slider     progressSlider;
    @FXML private Label      currentTime;
    @FXML private Label      totalTime;
    @FXML private StackPane  audioPanel;

    // PLAY/PAUSE icons
    @FXML private ImageView playIcon;    // injected from FXML
    @FXML private ImageView pauseIcon;   // injected from FXML

    private MediaPlayer mediaPlayer;
    private boolean userIsDragging = false;

    private final Image PLAY_IMG  = new Image(getClass().getResource("/icons/Play.png").toExternalForm());
    private final Image PAUSE_IMG = new Image(getClass().getResource("/icons/Pause.png").toExternalForm());

    @FXML
    private void initialize(){
        audioPanel.maxWidthProperty().bind(
                Bindings.min(600, root.widthProperty().multiply(0.75))
        );
        audioPanel.prefWidthProperty().bind(audioPanel.maxWidthProperty());

        normaliseStyleClasses(root);

        playIcon.setImage(PLAY_IMG);
        pauseIcon.setImage(PAUSE_IMG);
        playIcon.setFitWidth(32); playIcon.setFitHeight(32);
        pauseIcon.setFitWidth(32); pauseIcon.setFitHeight(32);

        playIcon.setOnMouseClicked(e -> togglePlayPause());
        pauseIcon.setOnMouseClicked(e -> togglePlayPause());

        // initial visible state
        playIcon.setVisible(true);
        pauseIcon.setVisible(false);
        pauseIcon.setManaged(false);

        loadRandomAudioFromDB();
    }

    private void togglePlayPause(){
        if(mediaPlayer == null) return;
        if(mediaPlayer.getStatus()==MediaPlayer.Status.PLAYING){

            mediaPlayer.pause();
            playIcon.setVisible(true);
            pauseIcon.setVisible(false);
            pauseIcon.setManaged(false);
        } else {
            mediaPlayer.play();
            playIcon.setVisible(false);
            pauseIcon.setVisible(true);
            pauseIcon.setManaged(false);
        }
    }

    private void loadRandomAudioFromDB(){
        List<String> paths=new ArrayList<>();
        try(Connection c=DatabaseManager.connect();
            Statement st=c.createStatement();
            ResultSet rs=st.executeQuery("SELECT file_path FROM musics")){
            while(rs.next()){ paths.add(rs.getString("file_path")); }
        } catch(Exception e){ e.printStackTrace(); }

        if(paths.isEmpty()){
            totalTime.setText("No audio found");
            playIcon.setDisable(true);
            return;
        }
        String path=paths.get(new Random().nextInt(paths.size()));
        File f=new File(path);
        if(!f.exists()){
            totalTime.setText("Missing file!");
            playIcon.setDisable(true);
        } else {
            loadAudio(f.toURI().toString());
        }
    }

    private void loadAudio(String url){
        if(mediaPlayer!=null) mediaPlayer.dispose();
        mediaPlayer=new MediaPlayer(new Media(url));

        mediaPlayer.setOnReady(()->{
            Duration tot=mediaPlayer.getTotalDuration();
            totalTime.setText(format(tot));
            progressSlider.setMax(tot.toSeconds());
        });

        mediaPlayer.currentTimeProperty().addListener((o, ov, nv)->{
            if(!userIsDragging) progressSlider.setValue(nv.toSeconds());
            currentTime.setText(format(nv));
        });

        progressSlider.valueChangingProperty().addListener((o,oldV,newV)->{
            userIsDragging=newV;
            if(!newV) mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
        });
        progressSlider.setOnMouseReleased(e->
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue())));

        mediaPlayer.statusProperty().addListener((obs,old,st)->{
            if(st==MediaPlayer.Status.PLAYING){
                playIcon.setVisible(false); pauseIcon.setVisible(true);
            }else{
                playIcon.setVisible(true); pauseIcon.setVisible(false);
            }
        });
    }

    private static String format(Duration d){
        int sec=(int)d.toSeconds();
        return sec/60 + ":" + String.format("%02d", sec%60);
    }

    private static void normaliseStyleClasses(Parent root){
        for(Node n : root.lookupAll("*")){
            List<String> fixed = new ArrayList<>();
            for(String cls : n.getStyleClass()){
                if(cls.contains(",")){
                    for(String p : cls.split(",")) fixed.add(p.trim());
                } else fixed.add(cls);
            }
            n.getStyleClass().setAll(fixed);
        }
    }
}
