package org.example;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent splashRoot = FXMLLoader.load(getClass().getResource("/fxml/SplashScreen.fxml"));
        Scene splashScene = new Scene(splashRoot, 1024, 576);
        primaryStage.setTitle("Lucky Event");
        primaryStage.setScene(splashScene);
        primaryStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3.5));
        delay.setOnFinished(event -> AudioViewScreen.show(primaryStage));
        delay.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
