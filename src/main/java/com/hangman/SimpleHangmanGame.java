package com.hangman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class SimpleHangmanGame extends JFrame {
    
    private static final String[] CATEGORIES = {"Mythology", "Animals", "Countries", "Food", "Sports"};
    private static final String[][] WORDS = {
        {"ZEUS", "APOLLO", "ATHENA", "POSEIDON", "HERMES"}, // Mythology
        {"LION", "TIGER", "ELEPHANT", "GIRAFFE", "PANDA"}, // Animals
        {"FRANCE", "JAPAN", "BRAZIL", "EGYPT", "CANADA"}, // Countries
        {"PIZZA", "BURGER", "SUSHI", "PASTA", "STEAK"}, // Food
        {"SOCCER", "BASKETBALL", "TENNIS", "SWIMMING", "BOXING"} // Sports
    };
    
    private JLabel categoryLabel;
    private JLabel wordLabel;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel diamondsLabel;
    private JLabel trophiesLabel;
    private JPanel hangmanPanel;
    private JButton[] keyboardButtons;
    private JButton hintButton;
    private JButton pauseButton;
    
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
    private Random random;
    
    public SimpleHangmanGame() {
        initializeGame();
        createUI();
        setupEventHandlers();
        
        setTitle("Hangman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        startNewGame();
    }
    
    private void initializeGame() {
        gameExecutor = Executors.newSingleThreadExecutor();
        random = new Random();
        score = 0;
        level = 1;
        diamonds = 80;
        trophies = 10;
        gameOver = false;
        paused = false;
    }
    
    private void createUI() {
        setLayout(new BorderLayout(20, 20));
        
        // Top section with scores and pause button
        JPanel topSection = createTopSection();
        add(topSection, BorderLayout.NORTH);
        
        // Main game area
        JPanel gameArea = createGameArea();
        add(gameArea, BorderLayout.CENTER);
    }
    
    private JPanel createTopSection() {
        JPanel topSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        topSection.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        diamondsLabel = new JLabel("💎 " + diamonds);
        diamondsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        diamondsLabel.setForeground(Color.BLUE);
        
        trophiesLabel = new JLabel("🏆 " + trophies);
        trophiesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        trophiesLabel.setForeground(Color.ORANGE);
        
        pauseButton = new JButton("⏸");
        pauseButton.setPreferredSize(new Dimension(40, 40));
        pauseButton.setFocusPainted(false);
        
        topSection.add(diamondsLabel);
        topSection.add(trophiesLabel);
        topSection.add(pauseButton);
        
        return topSection;
    }
    
    private JPanel createGameArea() {
        JPanel gameArea = new JPanel(new GridLayout(1, 2, 30, 0));
        gameArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left side - word and keyboard
        JPanel leftSide = createLeftSide();
        
        // Right side - hangman and level
        JPanel rightSide = createRightSide();
        
        gameArea.add(leftSide);
        gameArea.add(rightSide);
        
        return gameArea;
    }
    
    private JPanel createLeftSide() {
        JPanel leftSide = new JPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Category
        categoryLabel = new JLabel();
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 20));
        categoryLabel.setForeground(Color.BLUE);
        categoryLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        categoryLabel.setPreferredSize(new Dimension(200, 40));
        categoryLabel.setMaximumSize(new Dimension(200, 40));
        
        // Word to guess
        wordLabel = new JLabel();
        wordLabel.setFont(new Font("Arial", Font.BOLD, 30));
        wordLabel.setForeground(Color.BLUE);
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Score and level
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        levelLabel = new JLabel("Level: " + level);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Keyboard
        keyboardButtons = createKeyboard();
        
        leftSide.add(Box.createVerticalGlue());
        leftSide.add(categoryLabel);
        leftSide.add(Box.createVerticalStrut(20));
        leftSide.add(wordLabel);
        leftSide.add(Box.createVerticalStrut(20));
        leftSide.add(scoreLabel);
        leftSide.add(Box.createVerticalStrut(10));
        leftSide.add(levelLabel);
        leftSide.add(Box.createVerticalStrut(20));
        
        leftSide.add(keyboardButtons[0].getParent().getParent()); // Add the entire keyboard panel
        
        leftSide.add(Box.createVerticalGlue());
        
        return leftSide;
    }
    
    private JPanel createRightSide() {
        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
        rightSide.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Level
        JLabel levelTitleLabel = new JLabel("Level " + level);
        levelTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        levelTitleLabel.setForeground(Color.BLUE);
        levelTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        levelTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Hangman panel
        hangmanPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHangman(g);
            }
        };
        hangmanPanel.setPreferredSize(new Dimension(300, 300));
        hangmanPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        hangmanPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Hint button
        hintButton = new JButton("💡 Hint (10 💎)");
        hintButton.setPreferredSize(new Dimension(120, 40));
        hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintButton.setFocusPainted(false);
        
        rightSide.add(Box.createVerticalGlue());
        rightSide.add(levelTitleLabel);
        rightSide.add(Box.createVerticalStrut(20));
        rightSide.add(hangmanPanel);
        rightSide.add(Box.createVerticalStrut(20));
        rightSide.add(hintButton);
        rightSide.add(Box.createVerticalGlue());
        
        return rightSide;
    }
    
    private JButton[] createKeyboard() {
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
        JButton[] buttons = new JButton[26];
        int buttonIndex = 0;
        
        JPanel keyboardPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        keyboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        for (String row : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
            for (char c : row.toCharArray()) {
                JButton button = new JButton(String.valueOf(c));
                button.setPreferredSize(new Dimension(30, 30));
                button.setFont(new Font("Arial", Font.BOLD, 12));
                button.setFocusPainted(false);
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                button.putClientProperty("letter", c);
                
                buttons[buttonIndex++] = button;
                rowPanel.add(button);
            }
            keyboardPanel.add(rowPanel);
        }
        
        return buttons;
    }
    
    private void setupEventHandlers() {
        // Keyboard button handlers
        for (JButton button : keyboardButtons) {
            button.addActionListener(e -> {
                Character letter = (Character) button.getClientProperty("letter");
                handleLetterGuess(letter);
            });
        }
        
        // Hint button
        hintButton.addActionListener(e -> useHint());
        
        // Pause button
        pauseButton.addActionListener(e -> togglePause());
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
                SwingUtilities.invokeLater(() -> hangmanPanel.repaint());
            }
            
            SwingUtilities.invokeLater(() -> {
                updateWordDisplay();
                updateKeyboardDisplay();
                checkGameStatus();
            });
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
        } else {
            pauseButton.setText("⏸");
        }
    }
    
    private void drawHangman(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.BLACK);
        
        // Draw gallows
        g2d.drawLine(50, 250, 250, 250); // Base
        g2d.drawLine(150, 250, 150, 50);  // Vertical post
        g2d.drawLine(150, 50, 200, 50);   // Top beam
        g2d.drawLine(200, 50, 200, 80);   // Rope
        
        // Draw hangman based on wrong guesses
        if (wrongGuesses >= 1) {
            // Head
            g2d.drawOval(180, 80, 40, 40);
        }
        if (wrongGuesses >= 2) {
            // Body
            g2d.drawLine(200, 120, 200, 180);
        }
        if (wrongGuesses >= 3) {
            // Left arm
            g2d.drawLine(200, 140, 170, 160);
        }
        if (wrongGuesses >= 4) {
            // Right arm
            g2d.drawLine(200, 140, 230, 160);
        }
        if (wrongGuesses >= 5) {
            // Left leg
            g2d.drawLine(200, 180, 170, 220);
        }
        if (wrongGuesses >= 6) {
            // Right leg
            g2d.drawLine(200, 180, 230, 220);
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
        for (JButton button : keyboardButtons) {
            char letter = (Character) button.getClientProperty("letter");
            if (guessedLetters.contains(letter)) {
                if (currentWord.indexOf(letter) >= 0) {
                    button.setBackground(Color.LIGHT_GRAY);
                    button.setEnabled(false);
                } else {
                    button.setBackground(Color.PINK);
                    button.setEnabled(false);
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
        String message = won ? 
            "Congratulations!\nYou won!\nScore: " + score + "\nLevel: " + level + "\nDiamonds: " + diamonds + "\nTrophies: " + trophies :
            "Game Over!\nThe word was: " + currentWord + "\nScore: " + score + "\nLevel: " + level;
        
        int choice = JOptionPane.showConfirmDialog(this, message, 
            won ? "Congratulations!" : "Game Over!", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        } else {
            System.exit(0);
        }
    }
    
    private void updateScoreDisplay() {
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + level);
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
        hangmanPanel.repaint();
        
        // Reset keyboard
        for (JButton button : keyboardButtons) {
            button.setBackground(null);
            button.setEnabled(true);
        }
        
        updateScoreDisplay();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            SimpleHangmanGame game = new SimpleHangmanGame();
            game.setVisible(true);
        });
    }
}
