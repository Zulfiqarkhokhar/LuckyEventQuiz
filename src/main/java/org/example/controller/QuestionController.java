package org.example.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.example.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionController {

    @FXML private Label questionLabel;
    @FXML private FlowPane answersFlow;
    @FXML private VBox questionBox;

    @FXML
    private void initialize(){
        // responsive font for the question
        Scene scene = questionLabel.getScene();
        if(scene != null) {
            bindFont(scene);
            bindResponsive(scene);
        }
        else {
            questionLabel.sceneProperty().addListener((o, ov, nv) -> bindFont(nv));
            questionBox.sceneProperty().addListener((obs, old, sc) -> bindResponsive(sc));
        }

        // load random question
        loadRandomQuestion();
    }

    private void bindFont(Scene scene){
        questionLabel.styleProperty().bind(
                Bindings.format("-fx-font-size: %.0fpx;",
                        scene.widthProperty().divide(32))
        );
    }

    private void loadRandomQuestion(){
        try(Connection c = DatabaseManager.connect()){
            // get single random question
            PreparedStatement qs = c.prepareStatement(
                    "SELECT * FROM questions ORDER BY RANDOM() LIMIT 1");
            ResultSet qrs = qs.executeQuery();
            if(!qrs.next()) return;

            int id = qrs.getInt("id");
            questionLabel.setText(qrs.getString("question_text"));

            PreparedStatement os = c.prepareStatement(
                    "SELECT option_text, is_correct FROM question_options WHERE question_id=?");
            os.setInt(1,id);
            ResultSet ors = os.executeQuery();

            List<String> opts = new ArrayList<>();
            while(ors.next()){
                opts.add(ors.getString("option_text"));
            }

            answersFlow.getChildren().clear();

            // A,B,C... prefix
            char prefix = 'A';
            for (int i = 0; i < opts.size(); i++) {
                String label = String.valueOf((char)('A' + i));  // A/B/C...
                Label title = new Label("Answer " + label);
                title.getStyleClass().add("title");

                Label desc  = new Label(opts.get(i));
                desc.getStyleClass().add("desc");
                desc.setWrapText(true);

                VBox card = new VBox(6, title, desc);
                card.getStyleClass().add("answer-option");

                answersFlow.getChildren().add(card);
            }


        }catch(Exception e){
            e.printStackTrace();
        }


    }


    private void bindResponsive(Scene scene){
        // top padding scales between 80px and 180px based on window height
        scene.heightProperty().addListener((obs, oldH, newH) -> {
            double h = newH.doubleValue();
            double topPadding = Math.min(180, Math.max(80, h * 0.2));
            questionBox.setPadding(new Insets(topPadding, 0, 0, h > 900 ? 100 : 40));

            // spacing between question and answers responsive too
            questionBox.setSpacing(h > 800 ? 48 : 24);
        });

        // call once to apply instantly
        double h = scene.getHeight();
        double topPadding = Math.min(180, Math.max(80, h * 0.2));
        questionBox.setPadding(new Insets(topPadding, 0, 0, h > 900 ? 100 : 40));
    }
}
