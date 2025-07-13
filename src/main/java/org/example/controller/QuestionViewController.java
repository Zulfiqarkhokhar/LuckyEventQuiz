package org.example.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;

/**
 * Controller for the whole Questions page.
 */
public class QuestionViewController {

    /* -- FXML-injected nodes -- */
    @FXML private VBox       questionList;
    @FXML private ScrollPane scrollPane;
    @FXML private Label      addQuestionText;
    @FXML private ImageView  addQuestionIcon;
    @FXML private Button     csvUploadBtn;
    @FXML private Button     resetBtn;
    @FXML private Button     saveBtn;

    /* -- Backing model -- */
    private final ObservableList<String> questions = FXCollections.observableArrayList(
            "This is the question number one which is editable when you click on it?",
            "This is the question number two which has a border when you hover over it?",
            "This is the question number three?",
            "This is the question number four?",
            "This is the question number five?"
    );

    /* ---------- FXML life-cycle ---------- */
    @FXML
    private void initialize() {
        refreshList();

        resetBtn.setOnAction(e -> {
            questions.clear();
            refreshList();
        });

        saveBtn.setOnAction(e -> {
            // TODO – persist however you like
            System.out.println("Saving questions: " + questions);
        });
    }

    /* ---------- Helpers ---------- */
    private void refreshList() {
        questionList.getChildren().clear();

        for (int i = 0; i < questions.size(); i++) {
            final int idx = i;

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/QuestionRow.fxml"));
                HBox row = loader.load();

                QuestionRowController rowCtrl = loader.getController();
                rowCtrl.setQuestionText(questions.get(idx));

                rowCtrl.setOnDelete(rc -> {
                    questions.remove(idx);
                    refreshList();
                });

                rowCtrl.setOnEdit(rc -> {
                    TextInputDialog dlg = new TextInputDialog(questions.get(idx));
                    dlg.setHeaderText("Edit question");
                    dlg.setContentText("Question:");
                    dlg.showAndWait().ifPresent(newText -> {
                        questions.set(idx, newText.trim());
                        refreshList();
                    });
                });

                questionList.getChildren().add(row);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
