package org.example.controller;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public class Snackbar {

    private enum Type {
        SUCCESS("#4CAF50"),
        ERROR("#cb0e0e"),
        INFO("#2196F3"),
        WARNING("#FF9800");

        final String color;
        Type(String c) { this.color = c; }
    }

    // Public helpers
    public static void success(Window w, String msg)                       { show(w, null, msg, Type.SUCCESS); }
    public static void success(Window w, String title, String msg)         { show(w, title, msg, Type.SUCCESS); }
    public static void error(Window w, String msg)                         { show(w, null, msg, Type.ERROR); }
    public static void error(Window w, String title, String msg)           { show(w, title, msg, Type.ERROR); }
    public static void info(Window w, String msg)                          { show(w, null, msg, Type.INFO); }
    public static void info(Window w, String title, String msg)            { show(w, title, msg, Type.INFO); }
    public static void warning(Window w, String msg)                       { show(w, null, msg, Type.WARNING); }
    public static void warning(Window w, String title, String msg)         { show(w, title, msg, Type.WARNING); }

    private static void show(Window ownerWindow, String title, String message, Type type) {
        VBox box = new VBox(4);
        box.setStyle("-fx-background-color:" + type.color + ";" +
                "-fx-background-radius:8;" +
                "-fx-padding:10 18;");

        if (title != null && !title.isBlank()) {
            Label t = new Label(title);
            t.setStyle("-fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:15px;");
            box.getChildren().add(t);
        }

        if (message != null && !message.isBlank()) {
            Label m = new Label(message);
            m.setStyle("-fx-text-fill:white; -fx-font-size:14px;");
            m.setWrapText(true);
            box.getChildren().add(m);
        }

        Popup popup = new Popup();
        popup.getContent().add(box);
        popup.setAutoHide(true);
        popup.show(ownerWindow);

        // top center
        popup.setX(ownerWindow.getX() + ownerWindow.getWidth()/2 - box.getWidth()/2);
        popup.setY(ownerWindow.getY() + 30);

        PauseTransition d = new PauseTransition(Duration.seconds(2.5));
        d.setOnFinished(e -> popup.hide());
        d.play();
    }
}
