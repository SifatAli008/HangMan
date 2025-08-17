package com.hangman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HangmanGame extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/hangman.fxml"));
        
        // Increased window size to accommodate all content including instructions
        Scene scene = new Scene(root, 1200, 900);
        
        // Load CSS styles
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setTitle("🎯 Hangman Game - JavaFX Edition");
        primaryStage.setScene(scene);
        
        // Make window resizable for better user experience
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);
        
        // Center the window on screen
        primaryStage.centerOnScreen();
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
