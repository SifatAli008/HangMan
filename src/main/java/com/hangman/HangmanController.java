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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.stage.Modality;

import java.util.Set;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.util.Optional;

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
        // Clear any existing buttons and constraints
        keyboardGrid.getChildren().clear();
        keyboardGrid.getColumnConstraints().clear();
        keyboardGrid.getRowConstraints().clear();
        
        // Reduce gaps between buttons for better space utilization
        keyboardGrid.setHgap(3);
        keyboardGrid.setVgap(3);
        
        // Set up column constraints for 10 columns with larger width
        for (int i = 0; i < 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(45);
            col.setMinWidth(45);
            col.setMaxWidth(45);
            keyboardGrid.getColumnConstraints().add(col);
        }
        
        // Set up row constraints for 3 rows with larger height
        for (int i = 0; i < 3; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(45);
            row.setMinHeight(45);
            row.setMaxHeight(45);
            keyboardGrid.getRowConstraints().add(row);
        }
        
        String[] letters = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
        };
        
        int col = 0;
        int row = 0;
        
        for (String letter : letters) {
            Button button = new Button(letter);
            button.setPrefSize(45, 45);
            button.setMinSize(45, 45);
            button.setMaxSize(45, 45);
            button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            button.setStyle(getDefaultButtonStyle());
            button.setOnAction(e -> handleLetterGuess(letter.charAt(0)));
            
            // First row: A-J (10 letters)
            if (row == 0 && col >= 10) {
                col = 0;
                row = 1;
            }
            // Second row: K-T (10 letters)  
            else if (row == 1 && col >= 10) {
                col = 0;
                row = 2;
            }
            // Third row: U-Z (6 letters)
            
            keyboardGrid.add(button, col, row);
            col++;
        }
        
        // Debug: Print the grid layout
        System.out.println("Keyboard grid setup complete. Total buttons: " + keyboardGrid.getChildren().size());
        System.out.println("Columns: " + keyboardGrid.getColumnConstraints().size());
        System.out.println("Rows: " + keyboardGrid.getRowConstraints().size());
    }
    
    private String getDefaultButtonStyle() {
        return "-fx-background-color: #f8f9fa; " +
               "-fx-text-fill: #2c3e50; " +
               "-fx-background-radius: 6; " +
               "-fx-border-color: #6c757d; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 6; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold; " +
               "-fx-transition: all 0.2s ease-in-out;";
    }
    
    private String getCorrectButtonStyle() {
        return "-fx-background-color: #d4edda; " +
               "-fx-text-fill: #155724; " +
               "-fx-background-radius: 6; " +
               "-fx-border-color: #28a745; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 6; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold; " +
               "-fx-transition: all 0.3s ease-in-out;";
    }
    
    private String getWrongButtonStyle() {
        return "-fx-background-color: #f8d7da; " +
               "-fx-text-fill: #721c24; " +
               "-fx-background-radius: 6; " +
               "-fx-border-color: #dc3545; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 6; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold; " +
               "-fx-transition: all 0.3s ease-in-out;";
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
        char hintLetter = ' ';
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                hintLetter = c;
                break;
            }
        }
        
        if (hintLetter != ' ') {
            // Create custom hint dialog
            Dialog<String> hintDialog = new Dialog<>();
            hintDialog.setTitle("💡 Hint");
            hintDialog.initModality(Modality.APPLICATION_MODAL);
            
            // Set up the dialog content
            VBox content = new VBox(15);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: white; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10;");
            
            // Hint icon and title
            Label hintIcon = new Label("💡");
            hintIcon.setStyle("-fx-font-size: 48; -fx-text-fill: #f39c12;");
            
            Label hintTitle = new Label("Here's Your Hint!");
            hintTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            // Hint content
            Label hintText = new Label("The word contains the letter:");
            hintText.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d;");
            
            Label letterLabel = new Label(String.valueOf(hintLetter));
            letterLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #3498db; -fx-background-color: #ecf0f1; -fx-padding: 10 20; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 8;");
            letterLabel.setAlignment(Pos.CENTER);
            letterLabel.setPrefWidth(80);
            
            // Cost indicator
            Label costLabel = new Label("Cost: 10 💎");
            costLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            
            // Add all elements to content
            content.getChildren().addAll(hintIcon, hintTitle, hintText, letterLabel, costLabel);
            
            // Set up dialog buttons
            ButtonType useHintButton = new ButtonType("Use Hint", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            hintDialog.getDialogPane().getButtonTypes().addAll(useHintButton, cancelButton);
            
            // Style the buttons
            Button useHintBtn = (Button) hintDialog.getDialogPane().lookupButton(useHintButton);
            Button cancelBtn = (Button) hintDialog.getDialogPane().lookupButton(cancelButton);
            
            useHintBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
            cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
            
            hintDialog.getDialogPane().setContent(content);
            hintDialog.getDialogPane().setStyle("-fx-background-color: transparent;");
            
            // Show dialog and handle result
            Optional<String> result = hintDialog.showAndWait();
            if (result.isPresent() && result.get().equals("Use Hint")) {
                // Apply the hint
                handleLetterGuess(hintLetter);
            }
        } else {
            // No hints available
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("💡 Hint");
            alert.setHeaderText("No Hints Available");
            alert.setContentText("You've already guessed all the letters!");
            
            // Style the alert
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: white; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 10;");
            
            alert.showAndWait();
        }
    }
    
    private void showGameOverDialog() {
        String message;
        String title;
        String icon;
        
        if (gameLogic.isGameWon()) {
            title = "🎉 Congratulations!";
            icon = "🏆";
            message = "You won! The word was: " + gameLogic.getCurrentWord() + 
                     "\nScore: " + gameLogic.getScore() + 
                     "\nLevel: " + gameLogic.getLevel();
        } else {
            title = "💀 Game Over";
            icon = "💀";
            message = "You lost! The word was: " + gameLogic.getCurrentWord() + 
                     "\nScore: " + gameLogic.getScore() + 
                     "\nLevel: " + gameLogic.getLevel();
        }
        
        // Create custom game over dialog
        Dialog<String> gameOverDialog = new Dialog<>();
        gameOverDialog.setTitle(title);
        gameOverDialog.initModality(Modality.APPLICATION_MODAL);
        
        // Set up the dialog content
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        
        String borderColor = gameLogic.isGameWon() ? "#27ae60" : "#e74c3c";
        content.setStyle("-fx-background-color: white; -fx-border-color: " + borderColor + "; -fx-border-width: 2; -fx-border-radius: 10;");
        
        // Game result icon and title
        Label resultIcon = new Label(icon);
        resultIcon.setStyle("-fx-font-size: 48; -fx-text-fill: " + (gameLogic.isGameWon() ? "#27ae60" : "#e74c3c") + ";");
        
        Label resultTitle = new Label(title);
        resultTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Game result details
        Label resultDetails = new Label(message);
        resultDetails.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d; -fx-alignment: center;");
        resultDetails.setWrapText(true);
        resultDetails.setPrefWidth(300);
        
        // Add all elements to content
        content.getChildren().addAll(resultIcon, resultTitle, resultDetails);
        
        // Set up dialog buttons
        ButtonType newGameButton = new ButtonType("New Game", ButtonBar.ButtonData.OK_DONE);
        gameOverDialog.getDialogPane().getButtonTypes().add(newGameButton);
        
        // Style the button
        Button newGameBtn = (Button) gameOverDialog.getDialogPane().lookupButton(newGameButton);
        newGameBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        
        gameOverDialog.getDialogPane().setContent(content);
        gameOverDialog.getDialogPane().setStyle("-fx-background-color: transparent;");
        
        // Show dialog and handle result
        gameOverDialog.showAndWait();
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
