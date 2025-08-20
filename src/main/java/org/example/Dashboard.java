package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard {

    public static void show(Stage stage) {
        try {
            Parent dashboardRoot = FXMLLoader.load(Dashboard.class.getResource("/fxml/DashboardScreen.fxml")); //DashboardScreen.fxml
            Scene dashboardScene = new Scene(dashboardRoot, 1280, 720);
            dashboardScene.getStylesheets().add(Dashboard.class.getResource("/css/styles.css").toExternalForm());

            stage.setTitle("Lucky Event - Dashboard");
            stage.setScene(dashboardScene);
            stage.setFullScreen(true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
