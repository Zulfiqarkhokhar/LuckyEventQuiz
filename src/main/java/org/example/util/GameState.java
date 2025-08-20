package org.example.util;

public class GameState {
    private static String currentGame = "Question Game"; // default

    public static String getCurrentGame() {
        return currentGame;
    }

    public static void setCurrentGame(String game) {
        currentGame = game;
    }
}