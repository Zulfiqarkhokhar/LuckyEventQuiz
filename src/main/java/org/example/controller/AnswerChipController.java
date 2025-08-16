package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Controller for one editable answer “pill”.
 */
public class AnswerChipController {

    @FXML private HBox       chipRoot;
    @FXML private TextField  textField;

    private Runnable onDelete = () -> {};
    public  void setOnDelete(Runnable r) { onDelete = r; }

    /* expose text */
    public String getText()         { return textField.getText(); }
    public void   setText(String s) { textField.setText(s); }
    public void   requestFocus()    { textField.requestFocus(); }
    public TextField getTextField() {
        return textField;
    }


    /* delete icon click */
    @FXML private void handleDelete() { onDelete.run(); }

    @FXML
    private void initialize() {
        /* let modal fetch controller via parent.userData */
        chipRoot.setUserData(this);
    }
}
