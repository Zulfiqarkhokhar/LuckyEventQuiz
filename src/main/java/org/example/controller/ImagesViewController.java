package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.example.db.DatabaseManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImagesViewController {

    @FXML private TilePane imagePane;
    @FXML private ImageView addIcon, addDisabledIcon;
    @FXML private Label maxLabel;
    @FXML private VBox emptyState;

    private static final int MAX_IMAGES = 6;
    private final List<Node> cards = new ArrayList<>();

    @FXML
    private void initialize() {
        loadFromDB();
        updateAddAvailability();
        updateEmptyState();
    }

    /* === Load existing images from DB === */
    private void loadFromDB(){
        imagePane.getChildren().clear();
        cards.clear();
        try(Connection c = DatabaseManager.connect()){
            PreparedStatement st = c.prepareStatement("SELECT * FROM images");
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String fileName = rs.getString("file_name");
                String path = rs.getString("file_path");
                addImageCard(id, title, fileName, new File(path));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        updateEmptyState();
        updateAddAvailability();
    }

    /* === Create Card in UI === */
    private void addImageCard(int id, String title, String fileName, File file){
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/fxml/ImageCard.fxml"));
            Node cardNode = fx.load();
            ImageCardController ctrl = fx.getController();

            ctrl.setId(id);
            ctrl.setTitle(title);
            if(file.exists()){
                ctrl.setImage(new Image(file.toURI().toString(), true));
            }

            ctrl.setDeleteCallback(() -> {
                deleteFromDB(id, file);
                imagePane.getChildren().remove(cardNode);
                cards.remove(cardNode);
                updateEmptyState();
                updateAddAvailability();
                Snackbar.success(getWindow(),"Deleted");
            });

            // use modal for replace
            ctrl.setUploadCallback(c -> openUploadModal(ctrl));
            ctrl.setExpandCallback(this::showLightbox);

            imagePane.getChildren().add(cardNode);
            cards.add(cardNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* === Top + button === */
    @FXML private void handleAddImage(){
        if(cards.size() >= MAX_IMAGES){
            Snackbar.warning(getWindow(),"Limit","Max " + MAX_IMAGES);
            return;
        }
        // open drag-drop modal rather than direct FileChooser
        UploadImageModalController.open(getWindow(), file -> {
            try {
                File dest = copyToUploads(file);
                int newId = insertIntoDB(file.getName(), dest.getAbsolutePath());
                addImageCard(newId, file.getName(), file.getName(), dest);
                Snackbar.success(getWindow(),"Added");
                updateEmptyState();
                updateAddAvailability();
            } catch (Exception ex){
                ex.printStackTrace();
                Snackbar.error(getWindow(),"Upload failed");
            }
        });
    }

    /* === Replace inside card === */
    private void openUploadModal(ImageCardController ctrl){
        UploadImageModalController.open(getWindow(), file -> {
            try {
                File dest = copyToUploads(file);
                updateInDB(ctrl.getId(), file.getName(), dest.getAbsolutePath());
                ctrl.setImage(new Image(dest.toURI().toString(),true));
                ctrl.setTitle(file.getName());
                Snackbar.success(getWindow(),"Replaced");
            } catch (Exception ex){
                ex.printStackTrace();
                Snackbar.error(getWindow(),"Update failed");
            }
        });
    }

    /* === Copy to /uploads/images === */
    private File copyToUploads(File src) throws IOException {
        File destDir = new File("src/main/resources/uploads/images");
        destDir.mkdirs();
        File dest = new File(destDir, src.getName());
        try(FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dest)) {
            in.transferTo(out);
        }
        return dest;
    }


    /* === DB Ops === */
    private int insertIntoDB(String title,String path) throws SQLException {
        try(Connection c=DatabaseManager.connect()){
            PreparedStatement st=c.prepareStatement(
                    "INSERT INTO images(title,file_name,file_path) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1,title);
            st.setString(2,title);
            st.setString(3,path);
            st.executeUpdate();
            return st.getGeneratedKeys().getInt(1);
        }
    }
    private void updateInDB(int id,String fileName,String path)throws SQLException{
        try(Connection c=DatabaseManager.connect()){
            PreparedStatement st=c.prepareStatement(
                    "UPDATE images SET file_name=?,file_path=?,title=? WHERE id=?");
            st.setString(1,fileName);
            st.setString(2,path);
            st.setString(3,fileName);
            st.setInt(4,id);
            st.executeUpdate();
        }
    }
    private void deleteFromDB(int id,File file){
        try(Connection c=DatabaseManager.connect()){
            PreparedStatement st=c.prepareStatement("DELETE FROM images WHERE id=?");
            st.setInt(1,id);
            st.executeUpdate();
        }catch (Exception ignored){}
        if(file.exists()) file.delete();
    }

    /* === Footer === */
    @FXML private void resetImages(){ loadFromDB(); Snackbar.info(getWindow(),"Reset"); }
    @FXML private void saveImages(){ Snackbar.info(getWindow(),"Save","(not implemented)"); }

    /* === Helpers === */
    private void updateAddAvailability(){
        boolean maxed = cards.size()>=MAX_IMAGES;
        addIcon.setVisible(!maxed);
        addIcon.setManaged(!maxed);
        addDisabledIcon.setVisible(maxed);
        addDisabledIcon.setManaged(maxed);
        maxLabel.setVisible(maxed);
        maxLabel.setManaged(maxed);
    }
    private void updateEmptyState(){
        boolean e = cards.isEmpty();
        emptyState.setVisible(e);
        emptyState.setManaged(e);
    }
    private Window getWindow(){ return emptyState.getScene().getWindow(); }

    /* lightbox */
    private void showLightbox(ImageCardController cc){
        ImagePreviewModalController.open(getWindow(), cc.getPreviewImage().getImage());
    }
}
