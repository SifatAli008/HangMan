package com.hangman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.BlurType;

import java.util.Set;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.util.Optional;
import javafx.scene.layout.HBox;
import javafx.animation.PauseTransition;
import javafx.animation.KeyValue;
import javafx.scene.Node;

public class HangmanController {
    
    @FXML private Label categoryLabel;
    @FXML private Label wordLabel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label timerLabel;
    @FXML private Label wrongGuessesLabel;
    @FXML private Label hintsLeftLabel;
    @FXML private Canvas hangmanCanvas;
    @FXML private GridPane keyboardGrid;
    @FXML private VBox gameInfoBox;
    @FXML private Button newGameButton;
    @FXML private Button hintButton;
    
    private GameLogic gameLogic;
    private Timeline timerUpdate;
    private GraphicsContext gc;
    
    private boolean isPaused = false;
    
    // Enhanced visual effects
    private DropShadow defaultShadow;
    private DropShadow hoverShadow;
    private Glow successGlow;
    private Glow errorGlow;
    
    @FXML
    public void initialize() {
        gameLogic = new GameLogic();
        gc = hangmanCanvas.getGraphicsContext2D();
        
        // Initialize enhanced visual effects
        initializeVisualEffects();
        
        setupKeyboard();
        setupTimer();
        updateUI();
        
        // Set up keyboard input
        gameInfoBox.setOnKeyPressed(this::handleKeyPress);
        gameInfoBox.setFocusTraversable(true);
        gameInfoBox.requestFocus();
        
        // Set up button actions with enhanced animations
        newGameButton.setOnAction(e -> showPauseMenu());
        hintButton.setOnAction(e -> showHint());
        
        // Apply enhanced button effects
        applyButtonEffects(newGameButton);
        applyButtonEffects(hintButton);
        
        // Initial draw with fade-in effect
        addFadeInEffect(gameInfoBox);
        drawHangman();
    }
    
    private void initializeVisualEffects() {
        // Default shadow effect
        defaultShadow = new DropShadow();
        defaultShadow.setRadius(5.0);
        defaultShadow.setOffsetX(0.0);
        defaultShadow.setOffsetY(2.0);
        defaultShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        
        // Hover shadow effect
        hoverShadow = new DropShadow();
        hoverShadow.setRadius(8.0);
        hoverShadow.setOffsetX(0.0);
        hoverShadow.setOffsetY(4.0);
        hoverShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        
        // Success glow effect
        successGlow = new Glow();
        successGlow.setLevel(0.8);
        
        // Error glow effect
        errorGlow = new Glow();
        errorGlow.setLevel(0.6);
    }
    
    private void applyButtonEffects(Button button) {
        button.setEffect(defaultShadow);
        
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
            button.setEffect(hoverShadow);
        });
        
        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            button.setEffect(defaultShadow);
        });
    }
    
    private void addFadeInEffect(VBox container) {
        container.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), container);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    private void setupKeyboard() {
        // Clear any existing buttons and constraints
        keyboardGrid.getChildren().clear();
        keyboardGrid.getColumnConstraints().clear();
        keyboardGrid.getRowConstraints().clear();
        
        // Set up column constraints for 10 columns with optimal width
        for (int i = 0; i < 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(60);
            col.setMinWidth(60);
            col.setMaxWidth(60);
            keyboardGrid.getColumnConstraints().add(col);
        }
        
        // Set up row constraints for 3 rows with optimal height
        for (int i = 0; i < 3; i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(60);
            row.setMinHeight(60);
            row.setMaxHeight(60);
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
            button.setPrefSize(60, 60);
            button.setMinSize(60, 60);
            button.setMaxSize(60, 60);
            button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            button.getStyleClass().add("keyboard-button");
            button.setStyle(getDefaultButtonStyle());
            button.setOnAction(e -> handleLetterGuess(letter.charAt(0)));
            
            // Apply enhanced button effects
            applyButtonEffects(button);
            
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
        return "-fx-background-color: #ffffff; " +
               "-fx-text-fill: #2c3e50; " +
               "-fx-background-radius: 8; " +
               "-fx-border-color: #e9ecef; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 8; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold; " +
               "-fx-transition: all 0.2s ease-in-out; " +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 2, 0, 0, 1);";
    }
    
    private String getCorrectButtonStyle() {
        return "-fx-background-color: #d4edda; " +
               "-fx-text-fill: #155724; " +
               "-fx-background-radius: 8; " +
               "-fx-border-color: #28a745; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 8; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold; " +
               "-fx-transition: all 0.3s ease-in-out; " +
               "-fx-effect: dropshadow(gaussian, rgba(40, 167, 69, 0.3), 4, 0, 0, 2);";
    }
    
    private String getWrongButtonStyle() {
        return "-fx-background-color: #f8d7da; " +
               "-fx-text-fill: #721c24; " +
               "-fx-background-radius: 8; " +
               "-fx-border-color: #dc3545; " +
               "-fx-border-width: 2; " +
               "-fx-border-radius: 8; " +
               "-fx-cursor: hand; " +
               "-fx-font-weight: bold; " +
               "-fx-transition: all 0.3s ease-in-out; " +
               "-fx-effect: dropshadow(gaussian, rgba(220, 53, 69, 0.3), 4, 0, 0, 2);";
    }
    
    private void setupTimer() {
        timerUpdate = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            updateTimer();
            if (gameLogic.isGameOver()) {
                timerUpdate.stop();
                // Use Platform.runLater to avoid animation conflicts
                Platform.runLater(() -> showGameOverDialog());
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
        
        boolean isCorrect = gameLogic.makeGuess(letter);
        
        // Enhanced visual feedback
        if (isCorrect) {
            addSuccessAnimation(wordLabel);
            addGlowEffect(wordLabel, successGlow);
        } else {
            addErrorAnimation(wrongGuessesLabel);
            addGlowEffect(wrongGuessesLabel, errorGlow);
        }
        
        updateUI();
        drawHangman();
        
        if (gameLogic.isGameOver()) {
            timerUpdate.stop();
            // Use Platform.runLater to avoid animation conflicts
            Platform.runLater(() -> showGameOverDialog());
        }
        
        // Update keyboard button states
        updateKeyboardState();
    }
    
    private void addSuccessAnimation(Node node) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), node);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        
        SequentialTransition sequence = new SequentialTransition(scaleUp, scaleDown);
        sequence.play();
    }
    
    private void addErrorAnimation(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setByX(-5);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
    
    private void addGlowEffect(Node node, Glow glow) {
        node.setEffect(glow);
        
        Timeline glowPulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
            new KeyFrame(Duration.millis(1000), new KeyValue(glow.levelProperty(), 0.8)),
            new KeyFrame(Duration.millis(2000), new KeyValue(glow.levelProperty(), 0.3))
        );
        glowPulse.setCycleCount(3);
        glowPulse.setOnFinished(e -> node.setEffect(null));
        glowPulse.play();
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
                        button.getStyleClass().add("correct-guess");
                        addSuccessAnimation(button);
                    } else {
                        button.setStyle(getWrongButtonStyle());
                        button.getStyleClass().add("wrong-guess");
                        addErrorAnimation(button);
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
        levelLabel.setText(String.valueOf(gameLogic.getLevel()));
        wrongGuessesLabel.setText(gameLogic.getWrongGuesses() + "/6");
        hintsLeftLabel.setText(String.valueOf(gameLogic.getMaxHints() - gameLogic.getHintCount()));
        
        // Update hint button state
        updateHintButton();
    }
    
    private void updateTimer() {
        int timeRemaining = gameLogic.getTimeRemaining();
        timerLabel.setText(timeRemaining + "s");
        
        if (timeRemaining <= 10) {
            timerLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #e74c3c, 3, 0, 0, 0);");
            // Add pulse effect for urgent timer
            addPulseEffect(timerLabel);
        } else if (timeRemaining <= 20) {
            timerLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        } else {
            timerLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        }
    }
    
    private void addPulseEffect(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(500), node);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }
    
    private void drawHangman() {
        gc.clearRect(0, 0, hangmanCanvas.getWidth(), hangmanCanvas.getHeight());
        
        int wrongGuesses = gameLogic.getWrongGuesses();
        
        // Draw gallows with modern styling and enhanced effects
        gc.setStroke(Color.web("#6c757d"));
        gc.setLineWidth(4);
        
        // Add shadow effect for depth
        gc.setGlobalAlpha(0.3);
        gc.setStroke(Color.web("#495057"));
        gc.setLineWidth(6);
        gc.strokeLine(82, 242, 82, 82);
        gc.strokeLine(82, 82, 202, 82);
        gc.strokeLine(202, 82, 202, 102);
        gc.setGlobalAlpha(1.0);
        
        // Main gallows
        gc.setStroke(Color.web("#6c757d"));
        gc.setLineWidth(4);
        
        // Vertical post
        gc.strokeLine(80, 240, 80, 80);
        // Horizontal beam
        gc.strokeLine(80, 80, 200, 80);
        // Rope
        gc.strokeLine(200, 80, 200, 100);
        
        // Draw hangman parts based on wrong guesses with modern colors and effects
        if (wrongGuesses >= 1) {
            // Head with enhanced styling
            gc.setStroke(Color.web("#495057"));
            gc.setLineWidth(4);
            gc.strokeOval(180, 100, 40, 40);
            
            // Add inner detail
            gc.setStroke(Color.web("#6c757d"));
            gc.setLineWidth(2);
            gc.strokeOval(185, 105, 30, 30);
        }
        if (wrongGuesses >= 2) {
            // Body
            gc.setStroke(Color.web("#495057"));
            gc.setLineWidth(4);
            gc.strokeLine(200, 140, 200, 200);
        }
        if (wrongGuesses >= 3) {
            // Left arm
            gc.strokeLine(200, 160, 170, 180);
        }
        if (wrongGuesses >= 4) {
            // Right arm
            gc.strokeLine(200, 160, 230, 180);
        }
        if (wrongGuesses >= 5) {
            // Left leg
            gc.strokeLine(200, 200, 170, 240);
        }
        if (wrongGuesses >= 6) {
            // Right leg
            gc.strokeLine(200, 200, 230, 240);
        }
        
        // Add enhanced shadow effect for the hangman
        if (wrongGuesses > 0) {
            gc.setGlobalAlpha(0.2);
            gc.setStroke(Color.web("#495057"));
            gc.setLineWidth(6);
            gc.strokeOval(180, 100, 40, 40);
            gc.setGlobalAlpha(1.0);
        }
    }
    
    private void showHint() {
        if (!gameLogic.canUseHint()) {
            showNoHintsAvailableDialog();
            return;
        }
        
        String currentWord = gameLogic.getCurrentWord();
        Set<Character> guessedLetters = gameLogic.getGuessedLetters();
        
        // Find a letter that hasn't been guessed yet
        char hintLetter = ' ';
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(Character.toUpperCase(c))) {
                hintLetter = Character.toUpperCase(c);
                break;
            }
        }
        
        if (hintLetter != ' ') {
            showHintDialog(hintLetter);
        } else {
            showNoHintsAvailableDialog();
        }
    }
    
    private void showHintDialog(char hintLetter) {
        Dialog<String> hintDialog = new Dialog<>();
        hintDialog.setTitle("💡 Smart Hint System");
        hintDialog.initModality(Modality.APPLICATION_MODAL);
        
        // Set dialog size for better proportions
        hintDialog.setWidth(450);
        hintDialog.setHeight(650);
        
        // Create content with modern clean theme
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        // Header section
        VBox headerSection = new VBox(8);
        headerSection.setAlignment(Pos.CENTER);
        
        Label hintIcon = new Label("💡");
        hintIcon.setStyle("-fx-font-size: 48; -fx-text-fill: #f39c12; -fx-effect: dropshadow(gaussian, #f39c12, 10, 0, 0, 0);");
        
        Label hintTitle = new Label("Smart Hint Available!");
        hintTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label hintSubtitle = new Label("AI-powered assistance to help you succeed");
        hintSubtitle.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        headerSection.getChildren().addAll(hintIcon, hintTitle, hintSubtitle);
        
        // Hint information section
        VBox infoSection = new VBox(15);
        infoSection.setAlignment(Pos.CENTER);
        infoSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-border-radius: 12; -fx-border-color: #e9ecef; -fx-border-width: 1;");
        
        Label infoTitle = new Label("The word contains this letter:");
        infoTitle.setStyle("-fx-font-size: 14; -fx-text-fill: #495057; -fx-font-weight: 600;");
        
        // Letter display - initially hidden with question mark
        VBox letterContainer = new VBox(8);
        letterContainer.setAlignment(Pos.CENTER);
        letterContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f8f9fa); -fx-padding: 20; -fx-border-radius: 12; -fx-border-color: #f39c12; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(243, 156, 18, 0.3), 6, 0, 0, 3);");
        
        Label letterLabel = new Label("?");
        letterLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #6c757d; -fx-font-family: 'Segoe UI';");
        letterLabel.setAlignment(Pos.CENTER);
        letterLabel.setId("hintLetterLabel"); // For later reference
        
        // Progress indicator
        HBox progressContainer = new HBox(8);
        progressContainer.setAlignment(Pos.CENTER);
        
        Label progressIcon = new Label("🎯");
        progressIcon.setStyle("-fx-font-size: 14;");
        
        Label progressLabel = new Label("Hint " + (gameLogic.getHintCount() + 1) + " of " + gameLogic.getMaxHints());
        progressLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #6c757d; -fx-font-weight: 600; -fx-padding: 6 12; -fx-background-color: #e9ecef; -fx-border-radius: 10;");
        
        progressContainer.getChildren().addAll(progressIcon, progressLabel);
        
        letterContainer.getChildren().addAll(letterLabel, progressContainer);
        
        // Cost and benefits section
        VBox costSection = new VBox(8);
        costSection.setAlignment(Pos.CENTER);
        costSection.setStyle("-fx-background-color: #fff3cd; -fx-padding: 15; -fx-border-radius: 10; -fx-border-color: #ffeaa7; -fx-border-width: 1;");
        
        Label costTitle = new Label("💎 Hint Benefits");
        costTitle.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #856404;");
        
        VBox benefitsList = new VBox(4);
        benefitsList.setAlignment(Pos.CENTER_LEFT);
        
        Label benefit1 = new Label("• Reveals one correct letter");
        benefit1.setStyle("-fx-font-size: 11; -fx-text-fill: #856404;");
        
        Label benefit2 = new Label("• Helps you progress faster");
        benefit2.setStyle("-fx-font-size: 11; -fx-text-fill: #856404;");
        
        Label benefit3 = new Label("• Limited to 3 hints per game");
        benefit3.setStyle("-fx-font-size: 11; -fx-text-fill: #856404; -fx-font-weight: bold;");
        
        benefitsList.getChildren().addAll(benefit1, benefit2, benefit3);
        costSection.getChildren().addAll(costTitle, benefitsList);
        
        infoSection.getChildren().addAll(infoTitle, letterContainer, costSection);
        
        // Action buttons - horizontal layout with smaller size
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setStyle("-fx-padding: 15;");
        
        Button useHintBtn = new Button("🎯 Use Hint");
        useHintBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #27ae60, #2ecc71); -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 12 20; -fx-background-radius: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(39, 174, 96, 0.4), 4, 0, 0, 2); -fx-min-width: 120; -fx-min-height: 40;");
        useHintBtn.setOnAction(e -> {
            // Reveal the hint letter
            letterLabel.setText(String.valueOf(hintLetter));
            letterLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #f39c12; -fx-font-family: 'Segoe UI';");
            
            // Apply the hint
            gameLogic.incrementHintCount();
            handleLetterGuess(hintLetter);
            updateUI();
            
            // Close dialog after a short delay to show the revealed letter
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> hintDialog.close());
            delay.play();
        });
        
        Button closeBtn = new Button("❌ Close");
        closeBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #95a5a6, #7f8c8d); -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 12 20; -fx-background-radius: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(149, 165, 166, 0.4), 4, 0, 0, 2); -fx-min-width: 120; -fx-min-height: 40;");
        closeBtn.setOnAction(e -> hintDialog.close());
        
        buttonContainer.getChildren().addAll(useHintBtn, closeBtn);
        
        // Assemble content
        content.getChildren().addAll(headerSection, infoSection, buttonContainer);
        
        hintDialog.getDialogPane().setContent(content);
        hintDialog.getDialogPane().setStyle("-fx-background-color: transparent;");
        
        // Add close button type to dialog for proper handling
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        hintDialog.getDialogPane().getButtonTypes().add(closeButtonType);
        
        // Style the dialog's close button
        Button dialogCloseBtn = (Button) hintDialog.getDialogPane().lookupButton(closeButtonType);
        dialogCloseBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: transparent; -fx-padding: 0; -fx-min-width: 0; -fx-min-height: 0;");
        
        hintDialog.showAndWait();
    }
    
    private void showNoHintsAvailableDialog() {
        Dialog<String> noHintDialog = new Dialog<>();
        noHintDialog.setTitle("🔒 No Hints Available");
        noHintDialog.initModality(Modality.APPLICATION_MODAL);
        
        noHintDialog.setWidth(350);
        noHintDialog.setHeight(280);
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        // Icon and title
        Label noHintIcon = new Label("🔒");
        noHintIcon.setStyle("-fx-font-size: 48; -fx-text-fill: #6c757d; -fx-effect: dropshadow(gaussian, #6c757d, 10, 0, 0, 0);");
        
        Label noHintTitle = new Label("No Hints Available");
        noHintTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Message section
        VBox messageSection = new VBox(10);
        messageSection.setAlignment(Pos.CENTER);
        messageSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-radius: 10; -fx-border-color: #e9ecef; -fx-border-width: 1;");
        
        Label messageTitle = new Label("🎉 Great Progress!");
        messageTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        Label messageText = new Label("You've already discovered all the letters!\nYou're doing amazing - keep going!");
        messageText.setStyle("-fx-font-size: 12; -fx-text-fill: #495057; -fx-alignment: center; -fx-text-alignment: center; -fx-wrap-text: true; -fx-pref-width: 250;");
        
        messageSection.getChildren().addAll(messageTitle, messageText);
        
        // Close button
        Button closeBtn = new Button("✅ Got it!");
        closeBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #3498db, #5dade2); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52, 152, 219, 0.4), 4, 0, 0, 2); -fx-min-width: 120; -fx-min-height: 40;");
        closeBtn.setOnAction(e -> noHintDialog.close());
        
        content.getChildren().addAll(noHintIcon, noHintTitle, messageSection, closeBtn);
        noHintDialog.getDialogPane().setContent(content);
        noHintDialog.getDialogPane().setStyle("-fx-background-color: transparent;");
        
        // Add close button type to dialog for proper handling
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        noHintDialog.getDialogPane().getButtonTypes().add(closeButtonType);
        
        // Style the dialog's close button
        Button dialogCloseBtn = (Button) noHintDialog.getDialogPane().lookupButton(closeButtonType);
        dialogCloseBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: transparent; -fx-padding: 0; -fx-min-width: 0; -fx-min-height: 0;");
        
        noHintDialog.showAndWait();
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
        Dialog<ButtonType> gameOverDialog = new Dialog<>();
        gameOverDialog.setTitle(title);
        gameOverDialog.initModality(Modality.APPLICATION_MODAL);
        
        // Set up the dialog content
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        
        if (gameLogic.isGameWon()) {
            content.setStyle("-fx-background-color: linear-gradient(to bottom, #4ecdc4, #44a08d); -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(78, 205, 196, 0.3), 10, 0, 0, 5);");
            
            // Success icon
            Label successIcon = new Label("🏆");
            successIcon.setStyle("-fx-font-size: 64; -fx-text-fill: #feca57; -fx-effect: dropshadow(gaussian, #feca57, 15, 0, 0, 0);");
            
            // Title
            Label titleLabel = new Label("Congratulations! You Won!");
            titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.2), 2, 0, 0, 1);");
            
            // Score and level
            Label scoreLabel = new Label("Final Score: " + gameLogic.getScore());
            scoreLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
            
            Label levelLabel = new Label("Level Reached: " + gameLogic.getLevel());
            levelLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
            
            content.getChildren().addAll(successIcon, titleLabel, scoreLabel, levelLabel);
        } else {
            content.setStyle("-fx-background-color: linear-gradient(to bottom, #ff6b6b, #ee5a24); -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(255, 107, 107, 0.3), 10, 0, 0, 5);");
            
            // Game over icon
            Label gameOverIcon = new Label("💀");
            gameOverIcon.setStyle("-fx-font-size: 64; -fx-text-fill: #ffffff; -fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.2), 2, 0, 0, 1);");
            
            // Title
            Label titleLabel = new Label("Game Over! Better luck next time!");
            titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.2), 2, 0, 0, 1);");
            
            // Word and score
            Label wordLabel = new Label("The word was: " + gameLogic.getCurrentWord());
            wordLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
            
            Label scoreLabel = new Label("Final Score: " + gameLogic.getScore());
            scoreLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
            
            content.getChildren().addAll(gameOverIcon, titleLabel, wordLabel, scoreLabel);
        }
        
        // Buttons
        ButtonType newGameButton = new ButtonType("🔄 New Game", ButtonBar.ButtonData.OK_DONE);
        ButtonType quitButton = new ButtonType("🚪 Quit", ButtonBar.ButtonData.CANCEL_CLOSE);
        gameOverDialog.getDialogPane().getButtonTypes().addAll(newGameButton, quitButton);
        
        // Style the buttons
        Button newGameBtn = (Button) gameOverDialog.getDialogPane().lookupButton(newGameButton);
        Button quitBtn = (Button) gameOverDialog.getDialogPane().lookupButton(quitButton);
        
        newGameBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #4ecdc4, #44a08d); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 12 25; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(78, 205, 196, 0.4), 5, 0, 0, 2);");
        quitBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #ff6b6b, #ee5a24); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 12 25; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(255, 107, 107, 0.4), 5, 0, 0, 2);");
        
        gameOverDialog.getDialogPane().setContent(content);
        gameOverDialog.getDialogPane().setStyle("-fx-background-color: transparent;");
        
        // Handle button actions
        Optional<ButtonType> result = gameOverDialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == newGameButton) {
                startNewGame();
            } else if (result.get() == quitButton) {
                Platform.exit();
            }
        }
    }
    
    private void startNewGame() {
        gameLogic.resetGame();
        isPaused = false;
        newGameButton.setText("⏸");
        newGameButton.setStyle("-fx-font-size: 18; -fx-padding: 8; -fx-background-color: transparent; -fx-border-color: #2c3e50; -fx-border-width: 2; -fx-border-radius: 20; -fx-min-width: 40; -fx-min-height: 40; -fx-cursor: hand;");
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
                button.getStyleClass().remove("correct-guess");
                button.getStyleClass().remove("wrong-guess");
            }
        }
    }
    
    private void showPauseMenu() {
        if (isPaused) {
            // Resume game
            resumeGame();
        } else {
            // Pause game
            pauseGame();
        }
    }
    
    private void pauseGame() {
        isPaused = true;
        if (timerUpdate != null) {
            timerUpdate.pause();
        }
        // Also pause the GameLogic timer
        gameLogic.pauseTimer();
        newGameButton.setText("▶️");
        newGameButton.setStyle("-fx-font-size: 18; -fx-padding: 8; -fx-background-color: #27ae60; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 20; -fx-min-width: 40; -fx-min-height: 40; -fx-cursor: hand; -fx-text-fill: white;");
        
        // Show pause menu dialog
        showPauseMenuDialog();
    }
    
    private void resumeGame() {
        isPaused = false;
        if (timerUpdate != null) {
            timerUpdate.play();
        }
        // Also resume the GameLogic timer
        gameLogic.resumeTimer();
        newGameButton.setText("⏸");
        newGameButton.setStyle("-fx-font-size: 18; -fx-padding: 8; -fx-background-color: transparent; -fx-border-color: #2c3e50; -fx-border-width: 2; -fx-border-radius: 20; -fx-min-width: 40; -fx-min-height: 40; -fx-cursor: hand;");
        
        // Hide pause menu if visible
        hidePauseMenu();
    }
    
    private void showPauseMenuDialog() {
        Dialog<String> pauseMenuDialog = new Dialog<>();
        pauseMenuDialog.setTitle("⏸ Game Paused");
        pauseMenuDialog.initModality(Modality.APPLICATION_MODAL);
        
        // Set dialog size for better proportions and to prevent button cutoff
        pauseMenuDialog.setWidth(500);
        pauseMenuDialog.setHeight(650);
        
        // Pause icon
        Label pauseIcon = new Label("⏸");
        pauseIcon.setStyle("-fx-font-size: 65; -fx-text-fill: #ff6b6b; -fx-effect: dropshadow(gaussian, #ff6b6b, 15, 0, 0, 0);");
        pauseIcon.setAlignment(Pos.CENTER);
        
        // Menu title
        Label menuTitle = new Label("Game Paused");
        menuTitle.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-effect: dropshadow(gaussian, rgba(44, 62, 80, 0.2), 2, 0, 0, 1);");
        menuTitle.setAlignment(Pos.CENTER);
        
        // Game status
        VBox gameStatus = new VBox(12);
        gameStatus.setAlignment(Pos.CENTER);
        gameStatus.setStyle("-fx-background-color: rgba(52, 152, 219, 0.1); -fx-padding: 20; -fx-border-color: #3498db; -fx-border-width: 1; -fx-border-radius: 12;");
        
        Label wordStatus = new Label("Word: " + gameLogic.getCurrentWord().replaceAll(".", "_"));
        wordStatus.setStyle("-fx-font-size: 16; -fx-text-fill: #2c3e50; -fx-font-family: 'Consolas'; -fx-letter-spacing: 3; -fx-font-weight: bold;");
        wordStatus.setAlignment(Pos.CENTER);
        
        Label timeStatus = new Label("Time: " + gameLogic.getTimeRemaining() + "s");
        timeStatus.setStyle("-fx-font-size: 16; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        timeStatus.setAlignment(Pos.CENTER);
        
        Label wrongGuessesStatus = new Label("Wrong: " + gameLogic.getWrongGuesses() + "/6");
        wrongGuessesStatus.setStyle("-fx-font-size: 16; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        wrongGuessesStatus.setAlignment(Pos.CENTER);
        
        gameStatus.getChildren().addAll(wordStatus, timeStatus, wrongGuessesStatus);
        
        // Menu options
        VBox menuOptions = new VBox(15);
        menuOptions.setAlignment(Pos.CENTER);
        
        Button resumeBtn = new Button("▶️ Resume Game");
        resumeBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #27ae60, #2ecc71); -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 25; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2); -fx-min-width: 250; -fx-min-height: 50;");
        resumeBtn.setOnAction(e -> {
            pauseMenuDialog.close();
            resumeGame();
        });
        
        Button newGameBtn = new Button("🔄 New Game");
        newGameBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #3498db, #5dade2); -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 25; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2); -fx-min-width: 250; -fx-min-height: 50;");
        newGameBtn.setOnAction(e -> {
            pauseMenuDialog.close();
            startNewGame();
        });
        
        Button quitBtn = new Button("🚪 Quit Game");
        quitBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #e74c3c, #ec7063); -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 25; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2); -fx-min-width: 250; -fx-min-height: 50;");
        quitBtn.setOnAction(e -> {
            Platform.exit();
        });
        
        menuOptions.getChildren().addAll(resumeBtn, newGameBtn, quitBtn);
        
        // Close button
        Button closeBtn = new Button("❌ Close");
        closeBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #95a5a6, #bdc3c7); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1); -fx-min-width: 120; -fx-min-height: 40;");
        closeBtn.setOnAction(e -> {
            pauseMenuDialog.close();
            hidePauseMenu();
        });
        
        // Layout
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        content.getChildren().addAll(pauseIcon, menuTitle, gameStatus, menuOptions, closeBtn);
        
        pauseMenuDialog.getDialogPane().setContent(content);
        pauseMenuDialog.getDialogPane().setStyle("-fx-background-color: transparent;");
        
        // Add close button type to dialog for proper handling
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        pauseMenuDialog.getDialogPane().getButtonTypes().add(closeButtonType);
        
        // Style the dialog's close button
        Button dialogCloseBtn = (Button) pauseMenuDialog.getDialogPane().lookupButton(closeButtonType);
        dialogCloseBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: transparent; -fx-padding: 0; -fx-min-width: 0; -fx-min-height: 0;");
        
        pauseMenuDialog.showAndWait();
    }
    
    private void hidePauseMenu() {
        // This method can be used to hide any visible pause menu elements
        // Currently the dialog handles its own closing
    }
    
    private void updateHintButton() {
        if (!gameLogic.canUseHint()) {
            hintButton.setDisable(true);
            hintButton.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 15 30; -fx-background-color: #6c757d; -fx-text-fill: #ffffff; -fx-background-radius: 25; -fx-opacity: 0.6; -fx-cursor: default; -fx-min-width: 200; -fx-min-height: 50; -fx-effect: dropshadow(gaussian, rgba(108, 117, 125, 0.2), 3, 0, 0, 1);");
        } else {
            hintButton.setDisable(false);
            hintButton.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 15 30; -fx-background-color: linear-gradient(to bottom, #f39c12, #e67e22); -fx-text-fill: white; -fx-background-radius: 25; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(243, 156, 18, 0.4), 6, 0, 0, 3); -fx-min-width: 200; -fx-min-height: 50; -fx-border-color: #d68910; -fx-border-width: 2; -fx-border-radius: 25;");
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
