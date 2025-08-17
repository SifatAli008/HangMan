package com.hangman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.Set;

public class HangmanController {
    
    @FXML private Label categoryLabel;
    @FXML private Label wordLabel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label timerLabel;
    @FXML private Canvas hangmanCanvas;
    @FXML private GridPane keyboardGrid;
    @FXML private VBox gameInfoBox;
    @FXML private Button newGameButton;
    @FXML private Button hintButton;
    
    private GameLogic gameLogic;
    private Timeline timerUpdate;
    private GraphicsContext gc;
    
    @FXML
    public void initialize() {
        gameLogic = new GameLogic();
        gc = hangmanCanvas.getGraphicsContext2D();
        
        setupKeyboard();
        setupTimer();
        updateUI();
        
        // Set up keyboard input
        gameInfoBox.setOnKeyPressed(this::handleKeyPress);
        gameInfoBox.setFocusTraversable(true);
        gameInfoBox.requestFocus();
        
        // Set up button actions
        newGameButton.setOnAction(e -> startNewGame());
        hintButton.setOnAction(e -> showHint());
        
        // Initial draw
        drawHangman();
    }
    
    private void setupKeyboard() {
        String[] letters = {
            "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L",
            "Z", "X", "C", "V", "B", "N", "M"
        };
        
        int col = 0;
        int row = 0;
        
        for (String letter : letters) {
            Button button = new Button(letter);
            button.setPrefSize(40, 40);
            button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            button.setStyle(getDefaultButtonStyle());
            button.setOnAction(e -> handleLetterGuess(letter.charAt(0)));
            
            if (col >= 10) {
                col = 0;
                row++;
            }
            
            keyboardGrid.add(button, col, row);
            col++;
        }
    }
    
    private String getDefaultButtonStyle() {
        return "-fx-background-color: #f8f9fa; " +
               "-fx-text-fill: #2c3e50; " +
               "-fx-background-radius: 6; " +
               "-fx-border-color: #6c757d; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 6; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold;";
    }
    
    private String getCorrectButtonStyle() {
        return "-fx-background-color: #d4edda; " +
               "-fx-text-fill: #155724; " +
               "-fx-background-radius: 6; " +
               "-fx-border-color: #28a745; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 6; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold;";
    }
    
    private String getWrongButtonStyle() {
        return "-fx-background-color: #f8d7da; " +
               "-fx-text-fill: #721c24; " +
               "-fx-background-radius: 6; " +
               "-fx-border-color: #dc3545; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 6; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold;";
    }
    
    private void setupTimer() {
        timerUpdate = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            updateTimer();
            if (gameLogic.isGameOver()) {
                timerUpdate.stop();
                showGameOverDialog();
            }
        }));
        timerUpdate.setCycleCount(Timeline.INDEFINITE);
        timerUpdate.play();
    }
    
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode().isLetterKey()) {
            char letter = event.getCode().getChar().toUpperCase().charAt(0);
            handleLetterGuess(letter);
        }
    }
    
    private void handleLetterGuess(char letter) {
        if (gameLogic.isGameOver()) {
            return;
        }
        
        boolean correct = gameLogic.makeGuess(letter);
        updateUI();
        drawHangman();
        
        if (gameLogic.isGameOver()) {
            timerUpdate.stop();
            showGameOverDialog();
        }
        
        // Update keyboard button states
        updateKeyboardState();
    }
    
    private void updateKeyboardState() {
        Set<Character> guessedLetters = gameLogic.getGuessedLetters();
        
        for (javafx.scene.Node node : keyboardGrid.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                char letter = button.getText().charAt(0);
                
                if (guessedLetters.contains(letter)) {
                    if (gameLogic.getCurrentWord().indexOf(letter) != -1) {
                        button.setStyle(getCorrectButtonStyle());
                    } else {
                        button.setStyle(getWrongButtonStyle());
                    }
                    button.setDisable(true);
                }
            }
        }
    }
    
    private void updateUI() {
        categoryLabel.setText(gameLogic.getCurrentCategory());
        wordLabel.setText(gameLogic.getDisplayWord());
        scoreLabel.setText(String.valueOf(gameLogic.getScore()));
        levelLabel.setText("Level " + gameLogic.getLevel());
    }
    
    private void updateTimer() {
        int timeRemaining = gameLogic.getTimeRemaining();
        timerLabel.setText("Time: " + timeRemaining + "s");
        
        if (timeRemaining <= 10) {
            timerLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #e74c3c, 3, 0, 0, 0);");
        } else if (timeRemaining <= 20) {
            timerLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        } else {
            timerLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        }
    }
    
    private void drawHangman() {
        gc.clearRect(0, 0, hangmanCanvas.getWidth(), hangmanCanvas.getHeight());
        
        int wrongGuesses = gameLogic.getWrongGuesses();
        
        // Draw gallows with notebook style
        gc.setStroke(Color.web("#2c3e50"));
        gc.setLineWidth(3);
        
        // Vertical post
        gc.strokeLine(75, 220, 75, 60);
        // Horizontal beam
        gc.strokeLine(75, 60, 175, 60);
        // Rope
        gc.strokeLine(175, 60, 175, 80);
        
        // Draw hangman parts based on wrong guesses with colors
        if (wrongGuesses >= 1) {
            // Head
            gc.setStroke(Color.web("#e74c3c"));
            gc.setLineWidth(3);
            gc.strokeOval(155, 80, 40, 40);
        }
        if (wrongGuesses >= 2) {
            // Body
            gc.strokeLine(175, 120, 175, 170);
        }
        if (wrongGuesses >= 3) {
            // Left arm
            gc.strokeLine(175, 130, 145, 150);
        }
        if (wrongGuesses >= 4) {
            // Right arm
            gc.strokeLine(175, 130, 205, 150);
        }
        if (wrongGuesses >= 5) {
            // Left leg
            gc.strokeLine(175, 170, 145, 200);
        }
        if (wrongGuesses >= 6) {
            // Right leg
            gc.strokeLine(175, 170, 205, 200);
        }
        
        // Add some decorative elements
        gc.setStroke(Color.web("#bdc3c7"));
        gc.setLineWidth(1);
        gc.strokeLine(50, 220, 200, 220); // Base line
    }
    
    private void showHint() {
        String currentWord = gameLogic.getCurrentWord();
        Set<Character> guessedLetters = gameLogic.getGuessedLetters();
        
        // Find an unguessed letter
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("💡 Hint");
                alert.setHeaderText("Here's a hint!");
                alert.setContentText("The word contains the letter: " + c);
                alert.showAndWait();
                return;
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("💡 Hint");
        alert.setHeaderText("No hints available");
        alert.setContentText("You've already guessed all the letters!");
        alert.showAndWait();
    }
    
    private void showGameOverDialog() {
        String message;
        String title;
        
        if (gameLogic.isGameWon()) {
            title = "🎉 Congratulations!";
            message = "You won! The word was: " + gameLogic.getCurrentWord() + 
                     "\nScore: " + gameLogic.getScore() + 
                     "\nLevel: " + gameLogic.getLevel();
        } else {
            title = "💀 Game Over";
            message = "You lost! The word was: " + gameLogic.getCurrentWord() + 
                     "\nScore: " + gameLogic.getScore() + 
                     "\nLevel: " + gameLogic.getLevel();
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        
        startNewGame();
    }
    
    private void startNewGame() {
        gameLogic.resetGame();
        updateUI();
        drawHangman();
        resetKeyboard();
        timerUpdate.play();
        gameInfoBox.requestFocus();
    }
    
    private void resetKeyboard() {
        for (javafx.scene.Node node : keyboardGrid.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setStyle(getDefaultButtonStyle());
                button.setDisable(false);
            }
        }
    }
    
    public void shutdown() {
        if (timerUpdate != null) {
            timerUpdate.stop();
        }
        if (gameLogic != null) {
            gameLogic.shutdown();
        }
    }
}
