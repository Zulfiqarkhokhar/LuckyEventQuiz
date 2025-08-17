package org.example.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.example.db.DatabaseManager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Loads a random image-question from DB and displays it.
 */
public class ImageQuestionController {

    @FXML private BorderPane root;
    @FXML private Label      questionLabel;
    @FXML private ImageView  questionImage;

    @FXML
    private void initialize() {
        // Responsive max height of image
        questionImage.fitHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> root.getHeight() * 0.60,       // 60%
                        root.heightProperty()
                )
        );

        // Load from DB
        loadRandomFromDb();
    }

    private void loadRandomFromDb(){
        List<String> paths = new ArrayList<>();
        try(Connection con = DatabaseManager.connect();
            Statement st  = con.createStatement();
            ResultSet rs  = st.executeQuery("SELECT file_path FROM images"))
        {
            while(rs.next()){
                paths.add(rs.getString("file_path"));
            }
        }catch (Exception e){e.printStackTrace();}

        if(paths.isEmpty()){
            questionLabel.setText("No image questions found.");
            return;
        }

        // pick a random entry
        String path = paths.get(new Random().nextInt(paths.size()));

        File file = new File(path);
        if(!file.exists()){
            questionLabel.setText("File missing: "+file.getName());
        }else{
            questionLabel.setText("Guess what this image is about?");
            questionImage.setImage(new Image(file.toURI().toString(), true));
        }
    }

    /* If you want to load manually from another part */
    public void setQuestionText(String text){
        questionLabel.setText(text);
    }
    public void setImage(String url){
        questionImage.setImage(new Image(url,true));
    }
}
