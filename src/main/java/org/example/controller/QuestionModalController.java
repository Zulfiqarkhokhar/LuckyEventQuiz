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

/**
 * Controller for the Add / Edit Question modal.
 * – Centres itself over a dim overlay.
 * – Two answer “pills” per row via FlowPane.
 */
public class QuestionModalController {

    /* ──────────────── FXML references ─────────────── */
    @FXML private StackPane overlay;         // full-screen dim layer
    @FXML private TextField questionField;   // editable question text
    @FXML private FlowPane  answersPane;     // holds AnswerChip nodes

    /* ─────────────── callback wired by parent ─────────────── */
    private BiConsumer<String, List<String>> onSave = (q, a) -> {};
    public void setOnSave(BiConsumer<String, List<String>> cb) { onSave = cb; }

    /* ════════════════════════════════════════════════════════ */
    /*        STATIC FACTORY: open modal & return ctrl         */
    /* ════════════════════════════════════════════════════════ */
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

            /* ensure Scene root is a StackPane so we can overlay */
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

    /* ───────────────────── initialisation ─────────────────── */
    private void init(String question,
                      List<String> answers,
                      BiConsumer<String, List<String>> onSave) {
        this.onSave = onSave;
        questionField.setText(question == null ? "" : question);

        if (answers == null || answers.isEmpty()) {
            answers = List.of("Answer Number One",
                    "Answer Number Two",
                    "Answer Number Three",
                    "Answer Number Four");
        }
        answers.forEach(this::addChip);
    }

    /* ─────────── create & add a single AnswerChip ─────────── */
    private void addChip(String initialText) {
        try {
            FXMLLoader fx = new FXMLLoader(
                    getClass().getResource("/fxml/AnswerChip.fxml"));
            Node chipNode = fx.load();
            AnswerChipController chip = fx.getController();

            chip.setText(initialText == null ? "" : initialText);
            chip.setOnDelete(() -> answersPane.getChildren().remove(chipNode));

            /* two chips per row:
               (container width – hgap) / 2  */
            Region chipRegion = (Region) chipNode;
            chipRegion.prefWidthProperty().bind(
                    answersPane.widthProperty().subtract(28).divide(2));

            answersPane.getChildren().add(chipNode);

            if (initialText == null) chip.requestFocus();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /* ──────────────── FXML event handlers ─────────────── */
    @FXML private void handleAddAnswer() { addChip(null); }   // “Add Answer +”
    @FXML private void handleClose()    { close(); }          // ✕ icon

    @FXML
    private void handleSave() {
        String question = questionField.getText().trim();

        List<String> ans = new ArrayList<>();
        answersPane.getChildren().forEach(n -> {
            AnswerChipController c = (AnswerChipController) n.getUserData();
            if (c != null) ans.add(c.getText().trim());
        });

        onSave.accept(question, ans);
        close();
    }

    /* ─────────── internal helper to remove modal ─────────── */
    private void close() {
        ((StackPane) overlay.getParent()).getChildren().remove(overlay);
    }
}
