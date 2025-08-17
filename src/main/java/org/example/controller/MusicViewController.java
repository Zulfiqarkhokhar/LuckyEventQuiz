package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.example.db.DatabaseManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MusicViewController {

    @FXML private FlowPane musicFlow;
    @FXML private ImageView addIcon, addDisabledIcon;
    @FXML private Label maxLabel;

    // added: VBOX empty state
    @FXML private VBox emptyState;

    private static final int MAX_AUDIO = 5;
    private static final double GAP = 50, CARD_MIN_W = 400;

    private final List<Node> cardNodes = new ArrayList<>();

    @FXML
    public void initialize() {
        loadFromDB();
        musicFlow.widthProperty().addListener((o, ov, nv) -> resizeCards(nv.doubleValue()));
        updateAddAvailability();
        updateEmptyState();
    }

    private void resizeCards(double availableW) {
        int cols = availableW > 900 ? 2 : 1;
        double totalGap = GAP * (cols - 1);
        double width = (availableW - totalGap) / cols;
        if (width < CARD_MIN_W) { cols = 1; width = availableW; }
        musicFlow.setPrefWrapLength(cols * width + totalGap);
        for (Node n : cardNodes) ((Region)n).setPrefWidth(width);
    }

    /* DB LOAD */
    private void loadFromDB() {
        musicFlow.getChildren().clear();
        cardNodes.clear();
        try (Connection c = DatabaseManager.connect()) {
            PreparedStatement st = c.prepareStatement("SELECT * FROM musics");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String fileName = rs.getString("file_name");
                String path = rs.getString("file_path");
                addCard(id, title, fileName, new File(path));
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
        updateAddAvailability();
        updateEmptyState();
    }

    private MusicCardController addCard(int id, String title, String fileName, File path) throws IOException {
        FXMLLoader fx = new FXMLLoader(getClass().getResource("/fxml/MusicCard.fxml"));
        Node node = fx.load();
        MusicCardController ctrl = fx.getController();

        ctrl.setId(id);
        ctrl.setTitle(title);
        ctrl.setFileName(fileName);
        ctrl.setAudioFile(path);

        ctrl.setUploadCallback(card -> openUploadModal(ctrl));
        ctrl.setDeleteCallback(() -> {
            deleteFromDB(ctrl.getId(), path);
            musicFlow.getChildren().remove(node);
            cardNodes.remove(node);
            updateAddAvailability();
            updateEmptyState();
            Snackbar.success(getWindow(), "Deleted");
        });

        musicFlow.getChildren().add(node);
        cardNodes.add(node);
        resizeCards(musicFlow.getWidth());
        updateEmptyState();
        return ctrl;
    }

    /* ADD NEW AUDIO */
    @FXML
    private void handleAddMusic() {
        if (cardNodes.size() >= MAX_AUDIO) {
            Snackbar.warning(getWindow(),"Limit reached","Max " + MAX_AUDIO + " files.");
            return;
        }
        UploadAudioModalController.open(getWindow(), file -> {
            if (file != null) {
                saveFileAndAdd(file, null);  // null means new card
            }
        });
    }



    private void openUploadModal(MusicCardController updating){
        UploadAudioModalController.open(getWindow(), file -> {
            if(file != null) saveFileAndAdd(file, updating);
        });
    }
    private void saveFileAndAdd(File file, MusicCardController toUpdate){
        try {
            File destDir = new File("src/main/resources/uploads/audio");
            destDir.mkdirs();
            File dest = new File(destDir, file.getName());

            try(FileInputStream in = new FileInputStream(file);
                FileOutputStream out = new FileOutputStream(dest)){
                in.transferTo(out);
            }

            if(toUpdate == null){
                int id = insertIntoDB(dest.getName(), dest.getAbsolutePath());
                addCard(id, "New Music", dest.getName(), dest);
                Snackbar.success(getWindow(), "Uploaded");
            }
            else{
                updateInDB(toUpdate.getId(), dest.getName(), dest.getAbsolutePath());
                toUpdate.setFileName(dest.getName());
                toUpdate.setAudioFile(dest);
                Snackbar.success(getWindow(),"Replaced");
            }

            updateAddAvailability();
            updateEmptyState();
        }
        catch (Exception e){
            e.printStackTrace();
            Snackbar.error(getWindow(),"Upload failed.");
        }
    }


    /* DB helpers */
    private int insertIntoDB(String name, String path) throws SQLException {
        try (Connection c = DatabaseManager.connect()) {
            PreparedStatement st = c.prepareStatement(
                    "INSERT INTO musics(title,file_name,file_path) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1,"New Music");
            st.setString(2,name);
            st.setString(3,path);
            st.executeUpdate();
            return st.getGeneratedKeys().getInt(1);
        }
    }

    private void updateInDB(int id,String name,String path)throws SQLException{
        try(Connection c=DatabaseManager.connect()){
            PreparedStatement st = c.prepareStatement(
                    "UPDATE musics SET file_name=?,file_path=? WHERE id=?");
            st.setString(1,name);
            st.setString(2,path);
            st.setInt(3,id);
            st.executeUpdate();
        }
    }

    private void deleteFromDB(int id,File path){
        try(Connection c=DatabaseManager.connect()){
            PreparedStatement st=c.prepareStatement("DELETE FROM musics WHERE id=?");
            st.setInt(1,id);
            st.executeUpdate();
        }catch (Exception e){e.printStackTrace();}
        if(path.exists()){ path.delete(); }
    }

    @FXML private void resetMusic(){
        loadFromDB();
        Snackbar.info(getWindow(),"Refreshed");
    }

    @FXML private void saveMusic(){
        Snackbar.info(getWindow(),"Save","(Not used)");
    }

    private void updateAddAvailability(){
        boolean maxed = cardNodes.size()>=MAX_AUDIO;
        addIcon.setVisible(!maxed);
        addIcon.setManaged(!maxed);
        addDisabledIcon.setVisible(maxed);
        addDisabledIcon.setManaged(maxed);
        maxLabel.setVisible(maxed);
        maxLabel.setManaged(maxed);
    }

    /* ======> empty state */
    private void updateEmptyState(){
        boolean empty = cardNodes.isEmpty();
        emptyState.setVisible(empty);
        emptyState.setManaged(empty);
    }

    private Window getWindow(){ return musicFlow.getScene().getWindow(); }
}
