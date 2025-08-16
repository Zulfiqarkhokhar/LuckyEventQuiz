package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.example.db.DatabaseManager;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionViewController {

    @FXML private VBox questionContainer;
    @FXML private ImageView addIcon;
    @FXML private ImageView addDisabledIcon;
    @FXML private Label maxLabel;

    private static final int MAX_QUESTIONS = 5;

    @FXML
    public void initialize() {
        loadQuestionsFromDB();
        updateAddAvailability();
    }

    /* ========= Load existing questions ========= */
    private void loadQuestionsFromDB() {
        questionContainer.getChildren().clear();
        try (Connection conn = DatabaseManager.connect()) {
            PreparedStatement stQ = conn.prepareStatement("SELECT * FROM questions");
            ResultSet rsQ = stQ.executeQuery();
            while (rsQ.next()) {
                int qId = rsQ.getInt("id");
                String qText = rsQ.getString("question_text");
                List<String> opts = new ArrayList<>();

                PreparedStatement stO = conn.prepareStatement(
                        "SELECT option_text FROM question_options WHERE question_id=?");
                stO.setInt(1, qId);
                ResultSet rsO = stO.executeQuery();
                while (rsO.next()) {
                    opts.add(rsO.getString("option_text"));
                }
                addQuestionRow(qId, qText, opts);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addQuestionRow(int dbId, String text, List<String> options) throws IOException {
        FXMLLoader fx = new FXMLLoader(getClass().getResource("/fxml/QuestionRow.fxml"));
        Node rowNode = fx.load();
        QuestionRowController row = fx.getController();
        row.setQuestionText(text);
        row.setAnswers(options);
        row.setDeleteCallback(() -> {
            deleteQuestion(dbId);
            questionContainer.getChildren().remove(rowNode);
            updateAddAvailability();
            Snackbar.success(getWindow(), "Deleted question");
        });
        row.setEditCallback(() -> openEditModal(row, dbId));
        questionContainer.getChildren().add(rowNode);
    }

    /* ========= Add manually ========= */
    @FXML private void handleAddQuestion() {
        if (questionContainer.getChildren().size() >= MAX_QUESTIONS) {
            Snackbar.warning(getWindow(), "Limit reached", "Maximum "+MAX_QUESTIONS+" questions allowed.");
            return;
        }
        QuestionModalController.open(
                getWindow(),
                null,
                null,
                (q, a) -> {
                    if (!q.isBlank()) {
                        saveQuestionToDB(q, a, a.isEmpty() ? "" : a.get(0));
                        loadQuestionsFromDB();
                        updateAddAvailability();
                        Snackbar.success(getWindow(), "Question added");
                    }
                }
        );
    }

    /* ========= Edit ========= */
    private void openEditModal(QuestionRowController row, int dbId) {
        QuestionModalController.open(
                getWindow(),
                row.getQuestionText(),
                row.getAnswers(),
                (q, a) -> {
                    updateQuestionInDB(dbId, q, a);
                    loadQuestionsFromDB();
                    Snackbar.success(getWindow(), "Question updated");
                });
    }

    /* ========= Delete in DB ========= */
    private void deleteQuestion(int id) {
        try (Connection con = DatabaseManager.connect()) {
            PreparedStatement st = con.prepareStatement("DELETE FROM questions WHERE id=?");
            st.setInt(1, id);
            st.executeUpdate();

            PreparedStatement st2 = con.prepareStatement("DELETE FROM question_options WHERE question_id=?");
            st2.setInt(1, id);
            st2.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /* ========= Save to DB ========= */
    private void saveQuestionToDB(String qText, List<String> options, String correctAnswer) {
        try (Connection con = DatabaseManager.connect()) {
            PreparedStatement st = con.prepareStatement(
                    "INSERT INTO questions(question_text) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, qText);
            st.executeUpdate();
            int qId = st.getGeneratedKeys().getInt(1);

            PreparedStatement optSt = con.prepareStatement(
                    "INSERT INTO question_options(question_id, option_text, is_correct) VALUES (?, ?, ?)");
            for (String o : options) {
                optSt.setInt(1, qId);
                optSt.setString(2, o);
                optSt.setInt(3, o.equalsIgnoreCase(correctAnswer) ? 1 : 0);
                optSt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateQuestionInDB(int id, String q, List<String> options) {
        try (Connection con = DatabaseManager.connect()) {
            PreparedStatement st = con.prepareStatement("UPDATE questions SET question_text=? WHERE id=?");
            st.setString(1, q);
            st.setInt(2, id);
            st.executeUpdate();

            PreparedStatement del = con.prepareStatement("DELETE FROM question_options WHERE question_id=?");
            del.setInt(1, id);
            del.executeUpdate();

            PreparedStatement ins = con.prepareStatement(
                    "INSERT INTO question_options(question_id, option_text, is_correct) VALUES (?, ?, ?)");
            for (String o : options) {
                ins.setInt(1, id);
                ins.setString(2, o);
                ins.setInt(3, 0);
                ins.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /* ========= IMPORT w/limit ========= */
    @FXML
    private void handleCsvUpload() {
        int current = questionContainer.getChildren().size();
        if (current >= MAX_QUESTIONS) {
            Snackbar.warning(getWindow(), "Limit reached","Cannot import more than "+MAX_QUESTIONS+" questions.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select CSV or Excel File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV/Excel Files","*.csv","*.xlsx"));
        File file = chooser.showOpenDialog(getWindow());
        if (file == null) return;

        try {
            int imported = 0;
            int allowed = MAX_QUESTIONS - current;

            if (file.getName().toLowerCase().endsWith(".xlsx")) {
                imported = importExcelLimited(file, allowed);
            } else {
                imported = importCsvLimited(file, allowed);
            }

            loadQuestionsFromDB();
            updateAddAvailability();

            if (imported == 0) {
                Snackbar.warning(getWindow(),"Limit reached","No questions imported.");
            } else {
                Snackbar.success(getWindow(),"Imported " + imported + " question(s).");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.error(getWindow(),"Import failed");
        }
    }

    private int importCsvLimited(File f, int allowed) throws Exception {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null && count < allowed) {
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;
                String q = p[0].trim();
                String correct = p[p.length - 1].trim();
                List<String> opts = new ArrayList<>();
                for (int i = 1; i < p.length - 1; i++) {
                    if (!p[i].trim().isEmpty()) opts.add(p[i].trim());
                }
                saveQuestionToDB(q, opts, correct);
                count++;
            }
        }
        return count;
    }

    private int importExcelLimited(File f, int allowed) throws Exception {
        int count = 0;
        try (Workbook wb = WorkbookFactory.create(f)) {
            Sheet sheet = wb.getSheetAt(0);
            boolean first = true;
            for (Row row : sheet) {
                if (first) { first = false; continue; }
                if (count >= allowed) break;
                String q = row.getCell(0).toString().trim();
                int lastCol = row.getLastCellNum();
                String correct = row.getCell(lastCol - 1).toString().trim();
                List<String> opts = new ArrayList<>();
                for (int c = 1; c < lastCol - 1; c++) {
                    if (row.getCell(c) != null) {
                        String val = row.getCell(c).toString().trim();
                        if (!val.isEmpty()) opts.add(val);
                    }
                }
                saveQuestionToDB(q, opts, correct);
                count++;
            }
        }
        return count;
    }

    /* ========= Reset / Save Buttons ========= */
    @FXML private void resetQuestions() {
        questionContainer.getChildren().clear();
        loadQuestionsFromDB();
        updateAddAvailability();
        Snackbar.info(getWindow(),"Reset","All questions reloaded");
    }

    @FXML private void saveQuestions() {
        Snackbar.info(getWindow(),"Save","(Demo only — no action)");
    }

    /* ========= UI toggle ========= */
    private void updateAddAvailability() {
        int count = questionContainer.getChildren().size();
        boolean maxed = count >= MAX_QUESTIONS;
        addIcon.setVisible(!maxed);
        addIcon.setManaged(!maxed);
        addDisabledIcon.setVisible(maxed);
        addDisabledIcon.setManaged(maxed);
        maxLabel.setVisible(maxed);
        maxLabel.setManaged(maxed);
    }

    private Window getWindow() {
        return questionContainer.getScene().getWindow();
    }
}
