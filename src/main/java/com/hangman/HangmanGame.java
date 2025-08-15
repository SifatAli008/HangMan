package com.hangman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class HangmanGame extends Application {
    
    private static final String[] CATEGORIES = {"Mythology", "Animals", "Countries", "Food", "Sports"};
    private static final String[][] WORDS = {
        {"ZEUS", "APOLLO", "ATHENA", "POSEIDON", "HERMES"}, // Mythology
        {"LION", "TIGER", "ELEPHANT", "GIRAFFE", "PANDA"}, // Animals
        {"FRANCE", "JAPAN", "BRAZIL", "EGYPT", "CANADA"}, // Countries
        {"PIZZA", "BURGER", "SUSHI", "PASTA", "STEAK"}, // Food
        {"SOCCER", "BASKETBALL", "TENNIS", "SWIMMING", "BOXING"} // Sports
    };
    
    private Label categoryLabel;
    private Label wordLabel;
    private Label scoreLabel;
    private Label levelLabel;
    private Label diamondsLabel;
    private Label trophiesLabel;
    private Canvas hangmanCanvas;
    private GraphicsContext gc;
    private Button[] keyboardButtons;
    private Button hintButton;
    private Button pauseButton;
    
    private String currentWord;
    private String currentCategory;
    private char[] guessedWord;
    private List<Character> guessedLetters;
    private int wrongGuesses;
    private int score;
    private int level;
    private int diamonds;
    private int trophies;
    private boolean gameOver;
    private boolean paused;
    
    private ExecutorService gameExecutor;
    private ScheduledExecutorService timerExecutor;
    private Random random;
    
    @Override
    public void start(Stage primaryStage) {
        initializeGame();
        setupEventHandlers();
        
        Scene scene = new Scene(createMainLayout(), 800, 600);
        primaryStage.setTitle("Hangman Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        startNewGame();
    }
    
    private void initializeGame() {
        gameExecutor = Executors.newSingleThreadExecutor();
        timerExecutor = Executors.newScheduledThreadPool(1);
        random = new Random();
        score = 0;
        level = 1;
        diamonds = 80;
        trophies = 10;
        gameOver = false;
        paused = false;
    }
    
    private VBox createMainLayout() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: white;");
        
        // Top section with scores and pause button
        HBox topSection = createTopSection();
        
        // Main game area
        HBox gameArea = createGameArea();
        
        mainLayout.getChildren().addAll(topSection, gameArea);
        return mainLayout;
    }
    
    private HBox createTopSection() {
        HBox topSection = new HBox(30);
        topSection.setAlignment(Pos.CENTER);
        
        diamondsLabel = new Label("💎 " + diamonds);
        diamondsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        diamondsLabel.setTextFill(Color.BLUE);
        
        trophiesLabel = new Label("🏆 " + trophies);
        trophiesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        trophiesLabel.setTextFill(Color.GOLD);
        
        pauseButton = new Button("⏸");
        pauseButton.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-radius: 20;");
        pauseButton.setPrefSize(40, 40);
        
        topSection.getChildren().addAll(diamondsLabel, trophiesLabel, pauseButton);
        return topSection;
    }
    
    private HBox createGameArea() {
        HBox gameArea = new HBox(30);
        gameArea.setAlignment(Pos.CENTER);
        
        // Left side - word and keyboard
        VBox leftSide = createLeftSide();
        
        // Right side - hangman and level
        VBox rightSide = createRightSide();
        
        gameArea.getChildren().addAll(leftSide, rightSide);
        return gameArea;
    }
    
    private VBox createLeftSide() {
        VBox leftSide = new VBox(20);
        leftSide.setAlignment(Pos.CENTER);
        
        // Category
        categoryLabel = new Label();
        categoryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        categoryLabel.setTextFill(Color.BLUE);
        categoryLabel.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
        categoryLabel.setAlignment(Pos.CENTER);
        categoryLabel.setPrefWidth(200);
        
        // Word to guess
        wordLabel = new Label();
        wordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        wordLabel.setTextFill(Color.BLUE);
        wordLabel.setAlignment(Pos.CENTER);
        
        // Keyboard
        keyboardButtons = createKeyboard();
        
        leftSide.getChildren().addAll(categoryLabel, wordLabel);
        leftSide.getChildren().addAll(keyboardButtons);
        
        return leftSide;
    }
    
    private VBox createRightSide() {
        VBox rightSide = new VBox(20);
        rightSide.setAlignment(Pos.CENTER);
        
        // Level
        levelLabel = new Label("Level " + level);
        levelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        levelLabel.setTextFill(Color.BLUE);
        
        // Hangman canvas
        hangmanCanvas = new Canvas(300, 300);
        gc = hangmanCanvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        
        // Hint button
        hintButton = new Button("💡 Hint (10 💎)");
        hintButton.setStyle("-fx-background-color: yellow; -fx-font-weight: bold;");
        hintButton.setPrefSize(120, 40);
        
        rightSide.getChildren().addAll(levelLabel, hangmanCanvas, hintButton);
        return rightSide;
    }
    
    private Button[] createKeyboard() {
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
        Button[] buttons = new Button[26];
        int buttonIndex = 0;
        
        VBox keyboardLayout = new VBox(5);
        
        for (String row : rows) {
            HBox rowLayout = new HBox(5);
            rowLayout.setAlignment(Pos.CENTER);
            
            for (char c : row.toCharArray()) {
                Button button = new Button(String.valueOf(c));
                button.setPrefSize(35, 35);
                button.setStyle("-fx-background-color: lightblue; -fx-font-weight: bold;");
                button.setUserData(c);
                
                buttons[buttonIndex++] = button;
                rowLayout.getChildren().add(button);
            }
            keyboardLayout.getChildren().add(rowLayout);
        }
        
        return buttons;
    }
    
    private void setupEventHandlers() {
        // Keyboard button handlers
        for (Button button : keyboardButtons) {
            button.setOnAction(e -> handleLetterGuess((Character) button.getUserData()));
        }
        
        // Hint button
        hintButton.setOnAction(e -> useHint());
        
        // Pause button
        pauseButton.setOnAction(e -> togglePause());
    }
    
    private void handleLetterGuess(char letter) {
        if (gameOver || paused) return;
        
        gameExecutor.submit(() -> {
            if (guessedLetters.contains(letter)) return;
            
            guessedLetters.add(letter);
            
            boolean correctGuess = false;
            for (int i = 0; i < currentWord.length(); i++) {
                if (currentWord.charAt(i) == letter) {
                    guessedWord[i] = letter;
                    correctGuess = true;
                }
            }
            
            if (!correctGuess) {
                wrongGuesses++;
                updateHangman();
            }
            
            updateWordDisplay();
            updateKeyboardDisplay();
            checkGameStatus();
        });
    }
    
    private void useHint() {
        if (diamonds < 10 || gameOver || paused) return;
        
        diamonds -= 10;
        diamondsLabel.setText("💎 " + diamonds);
        
        // Find an unguessed letter
        for (int i = 0; i < currentWord.length(); i++) {
            if (guessedWord[i] == '_') {
                guessedWord[i] = currentWord.charAt(i);
                guessedLetters.add(currentWord.charAt(i));
                break;
            }
        }
        
        updateWordDisplay();
        updateKeyboardDisplay();
        checkGameStatus();
    }
    
    private void togglePause() {
        paused = !paused;
        if (paused) {
            pauseButton.setText("▶");
            // Pause game logic
        } else {
            pauseButton.setText("⏸");
            // Resume game logic
        }
    }
    
    private void updateHangman() {
        gc.clearRect(0, 0, 300, 300);
        
        // Draw gallows
        gc.strokeLine(50, 250, 250, 250); // Base
        gc.strokeLine(150, 250, 150, 50);  // Vertical post
        gc.strokeLine(150, 50, 200, 50);   // Top beam
        gc.strokeLine(200, 50, 200, 80);   // Rope
        
        // Draw hangman based on wrong guesses
        if (wrongGuesses >= 1) {
            // Head
            gc.strokeOval(180, 80, 40, 40);
        }
        if (wrongGuesses >= 2) {
            // Body
            gc.strokeLine(200, 120, 200, 180);
        }
        if (wrongGuesses >= 3) {
            // Left arm
            gc.strokeLine(200, 140, 170, 160);
        }
        if (wrongGuesses >= 4) {
            // Right arm
            gc.strokeLine(200, 140, 230, 160);
        }
        if (wrongGuesses >= 5) {
            // Left leg
            gc.strokeLine(200, 180, 170, 220);
        }
        if (wrongGuesses >= 6) {
            // Right leg
            gc.strokeLine(200, 180, 230, 220);
        }
    }
    
    private void updateWordDisplay() {
        StringBuilder display = new StringBuilder();
        for (char c : guessedWord) {
            display.append(c).append(" ");
        }
        wordLabel.setText(display.toString().trim());
    }
    
    private void updateKeyboardDisplay() {
        for (Button button : keyboardButtons) {
            char letter = (Character) button.getUserData();
            if (guessedLetters.contains(letter)) {
                if (currentWord.indexOf(letter) >= 0) {
                    button.setStyle("-fx-background-color: lightgreen; -fx-font-weight: bold;");
                    button.setDisable(true);
                } else {
                    button.setStyle("-fx-background-color: lightcoral; -fx-font-weight: bold;");
                    button.setDisable(true);
                }
            }
        }
    }
    
    private void checkGameStatus() {
        if (wrongGuesses >= 6) {
            gameOver = true;
            showGameOverDialog(false);
        } else if (new String(guessedWord).equals(currentWord)) {
            gameOver = true;
            score += 100;
            level++;
            diamonds += 20;
            trophies++;
            showGameOverDialog(true);
        }
        
        updateScoreDisplay();
    }
    
    private void showGameOverDialog(boolean won) {
        Alert alert = new Alert(won ? AlertType.INFORMATION : AlertType.WARNING);
        alert.setTitle("Game Over");
        alert.setHeaderText(won ? "Congratulations!" : "Game Over!");
        alert.setContentText(won ? 
            "You won! Score: " + score + "\nLevel: " + level + "\nDiamonds: " + diamonds + "\nTrophies: " + trophies :
            "The word was: " + currentWord + "\nScore: " + score + "\nLevel: " + level);
        
        ButtonType newGameButton = new ButtonType("New Game");
        ButtonType quitButton = new ButtonType("Quit");
        alert.getButtonTypes().setAll(newGameButton, quitButton);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == newGameButton) {
                startNewGame();
            } else {
                System.exit(0);
            }
        });
    }
    
    private void updateScoreDisplay() {
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level " + level);
        diamondsLabel.setText("💎 " + diamonds);
        trophiesLabel.setText("🏆 " + trophies);
    }
    
    private void startNewGame() {
        gameOver = false;
        wrongGuesses = 0;
        guessedLetters = new ArrayList<>();
        
        // Select random category and word
        int categoryIndex = random.nextInt(CATEGORIES.length);
        currentCategory = CATEGORIES[categoryIndex];
        currentWord = WORDS[categoryIndex][random.nextInt(WORDS[categoryIndex].length)];
        
        // Initialize guessed word
        guessedWord = new char[currentWord.length()];
        for (int i = 0; i < currentWord.length(); i++) {
            guessedWord[i] = '_';
        }
        
        // Reset UI
        categoryLabel.setText(currentCategory);
        updateWordDisplay();
        updateHangman();
        
        // Reset keyboard
        for (Button button : keyboardButtons) {
            button.setStyle("-fx-background-color: lightblue; -fx-font-weight: bold;");
            button.setDisable(false);
        }
        
        updateScoreDisplay();
    }
    
    @Override
    public void stop() {
        if (gameExecutor != null) {
            gameExecutor.shutdown();
        }
        if (timerExecutor != null) {
            timerExecutor.shutdown();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
