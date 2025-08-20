package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class QuestionModalController {

    @FXML private StackPane overlay;
    @FXML private TextField questionField;
    @FXML private FlowPane  answersPane;

    private BiConsumer<String, List<String>> onSave = (q, a) -> {};
    public void setOnSave(BiConsumer<String, List<String>> cb) { onSave = cb; }

    /* ==== Static creator (opens modal) ==== */
    public static QuestionModalController open(Window owner,
                                               String question,
                                               List<String> answers,
                                               BiConsumer<String, List<String>> onSave) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    QuestionModalController.class.getResource("/fxml/QuestionModal.fxml"));
            StackPane modalRoot = fx.load();
            QuestionModalController ctrl = fx.getController();
            ctrl.init(question, answers, onSave);

            Parent sceneRoot = owner.getScene().getRoot();
            StackPane stackRoot;
            if (sceneRoot instanceof StackPane) {
                stackRoot = (StackPane) sceneRoot;
            } else {
                stackRoot = new StackPane(sceneRoot);
                owner.getScene().setRoot(stackRoot);
            }
            stackRoot.getChildren().add(modalRoot);
            return ctrl;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot open Question modal", ex);
        }
    }

    /* ==== Initialiser ==== */
    private void init(String question,
                      List<String> answers,
                      BiConsumer<String, List<String>> onSave) {
        this.onSave = onSave;
        questionField.setText(question == null ? "" : question);

        if (question == null && (answers == null || answers.isEmpty())) {
            // ADD MODE → show 4 empty chips
            for (int i = 0; i < 4; i++) {
                addChip(null);
            }
        } else {
            // EDIT MODE → show existing answers
            if (answers != null) {
                answers.forEach(this::addChip);
            }
        }
    }

    /* ==== Add one chip to FlowPane ==== */
    private void addChip(String initialText) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    getClass().getResource("/fxml/AnswerChip.fxml"));
            Node chipNode = fx.load();
            AnswerChipController chip = fx.getController();

            if (initialText == null) {
                chip.setText("");
                chip.getTextField().setPromptText("Answer text…");
            } else {
                chip.setText(initialText);
            }

            // Important → so we can retrieve controller later in handleSave()
            chipNode.setUserData(chip);

            chip.setOnDelete(() -> answersPane.getChildren().remove(chipNode));

            // half-width behaviour
            Region chipRegion = (Region) chipNode;
            chipRegion.prefWidthProperty().bind(
                    answersPane.widthProperty().subtract(28).divide(2));

            answersPane.getChildren().add(chipNode);

            if (initialText == null) {
                chip.requestFocus();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /* ==== Events ==== */
    @FXML private void handleAddAnswer() { addChip(null); }
    @FXML private void handleClose() { close(); }

    @FXML
    private void handleSave() {
        String question = questionField.getText().trim();

        List<String> ans = new ArrayList<>();
        for (Node n : answersPane.getChildren()) {
            AnswerChipController c = (AnswerChipController) n.getUserData();
            if (c != null && !c.getText().trim().isEmpty()) {
                ans.add(c.getText().trim());
            }
        }

        onSave.accept(question, ans);
        close();
    }

    private void close() {
        // Remove this modal overlay from whatever StackPane it was added to
        Parent parent = overlay.getParent();
        if (parent instanceof StackPane stack) {
            stack.getChildren().remove(overlay);
        }
    }
}
