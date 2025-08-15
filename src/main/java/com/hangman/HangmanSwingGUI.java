package com.hangman;

import javax.swing.*;
import java.awt.*;
// Removed unused import
import java.awt.RenderingHints;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class HangmanSwingGUI extends JFrame {
    
    private static final String[] CATEGORIES = {"Animals", "Countries", "Food", "Sports"};
    private static final String[][] WORDS = {
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
    
    public HangmanSwingGUI() {
        initializeGame();
        createUI();
        setupEventHandlers();
        
        setTitle("Hangman Game - Swing GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
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
        JPanel topSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        topSection.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        topSection.setBackground(Color.WHITE);
        
        // Create styled panels for each stat
        JPanel diamondsPanel = createStatPanel("Diamonds", diamonds, new Color(0, 191, 255));
        JPanel trophiesPanel = createStatPanel("Trophies", trophies, new Color(255, 215, 0));
        JPanel scorePanel = createStatPanel("Score", score, new Color(255, 69, 0));
        
        diamondsLabel = (JLabel) diamondsPanel.getComponent(0);
        trophiesLabel = (JLabel) trophiesPanel.getComponent(0);
        scoreLabel = (JLabel) scorePanel.getComponent(0);
        
        pauseButton = new JButton("PAUSE");
        pauseButton.setPreferredSize(new Dimension(80, 50));
        pauseButton.setFont(new Font("Arial", Font.BOLD, 12));
        pauseButton.setBackground(Color.WHITE);
        pauseButton.setForeground(Color.BLACK);
        pauseButton.setBorder(BorderFactory.createRaisedBevelBorder());
        pauseButton.setFocusPainted(false);
        
        topSection.add(diamondsPanel);
        topSection.add(trophiesPanel);
        topSection.add(scorePanel);
        topSection.add(pauseButton);
        
        return topSection;
    }
    
    private JPanel createStatPanel(String title, int value, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        JLabel valueLabel = new JLabel(title + ": " + value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(Color.BLACK);
        
        panel.add(valueLabel);
        
        return panel;
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
        leftSide.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        leftSide.setBackground(Color.WHITE);
        
        // Category
        categoryLabel = new JLabel();
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        categoryLabel.setForeground(new Color(0, 191, 255));
        categoryLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 191, 255), 3),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        categoryLabel.setBackground(Color.WHITE);
        categoryLabel.setOpaque(true);
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        categoryLabel.setPreferredSize(new Dimension(250, 50));
        categoryLabel.setMaximumSize(new Dimension(250, 50));
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Word to guess
        wordLabel = new JLabel();
        wordLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        wordLabel.setForeground(Color.BLACK);
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wordLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Level
        levelLabel = new JLabel("Level: " + level);
        levelLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        levelLabel.setForeground(new Color(255, 215, 0));
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // Score
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(255, 69, 0));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
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
        rightSide.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        rightSide.setBackground(Color.WHITE);
        
        // Level
        JLabel levelTitleLabel = new JLabel("Level " + level);
        levelTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        levelTitleLabel.setForeground(new Color(255, 215, 0));
        levelTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        levelTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Hangman panel
        hangmanPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHangman(g);
            }
        };
        hangmanPanel.setPreferredSize(new Dimension(320, 320));
        hangmanPanel.setBackground(Color.WHITE);
        hangmanPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 191, 255), 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        hangmanPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Hint button
        hintButton = new JButton("Hint (10 Diamonds)");
        hintButton.setPreferredSize(new Dimension(150, 45));
        hintButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hintButton.setBackground(Color.WHITE);
        hintButton.setForeground(Color.BLACK);
        hintButton.setBorder(BorderFactory.createRaisedBevelBorder());
        hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintButton.setFocusPainted(false);
        
        rightSide.add(Box.createVerticalGlue());
        rightSide.add(levelTitleLabel);
        rightSide.add(Box.createVerticalStrut(20));
        rightSide.add(hangmanPanel);
        rightSide.add(Box.createVerticalStrut(25));
        rightSide.add(hintButton);
        rightSide.add(Box.createVerticalGlue());
        
        return rightSide;
    }
    
    private JButton[] createKeyboard() {
        // Create alphabetical layout: A-Z in 3 rows
        String[] rows = {"ABCDEFGHI", "JKLMNOPQR", "STUVWXYZ"};
        JButton[] buttons = new JButton[26];
        int buttonIndex = 0;
        
        JPanel keyboardPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        keyboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyboardPanel.setBackground(Color.WHITE);
        
        for (String row : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            rowPanel.setBackground(Color.WHITE);
            for (char c : row.toCharArray()) {
                JButton button = new JButton(String.valueOf(c));
                button.setPreferredSize(new Dimension(40, 40));
                button.setFont(new Font("Segoe UI", Font.BOLD, 16));
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                button.setFocusPainted(false);
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
            button.addActionListener(_ -> {
                Character letter = (Character) button.getClientProperty("letter");
                handleLetterGuess(letter);
            });
        }
        
        // Hint button
        hintButton.addActionListener(_ -> useHint());
        
        // Pause button
        pauseButton.addActionListener(_ -> togglePause());
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
        diamondsLabel.setText("Diamonds: " + diamonds);
        
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
            pauseButton.setText("RESUME");
        } else {
            pauseButton.setText("PAUSE");
        }
    }
    
    private void drawHangman(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(4));
        
        // Draw gallows with better colors
        g2d.setColor(new Color(139, 69, 19)); // Brown wood
        g2d.drawLine(60, 260, 260, 260); // Base
        g2d.drawLine(160, 260, 160, 60);  // Vertical post
        g2d.drawLine(160, 60, 210, 60);   // Top beam
        g2d.drawLine(210, 60, 210, 90);   // Rope
        
        // Draw hangman based on wrong guesses with better styling
        if (wrongGuesses >= 1) {
            // Head
            g2d.setColor(new Color(255, 218, 185)); // Peach skin
            g2d.fillOval(190, 90, 40, 40);
            g2d.setColor(new Color(139, 69, 19)); // Brown outline
            g2d.drawOval(190, 90, 40, 40);
        }
        if (wrongGuesses >= 2) {
            // Body
            g2d.setColor(new Color(70, 130, 180)); // Blue shirt
            g2d.fillRect(195, 130, 10, 50);
            g2d.setColor(new Color(139, 69, 19)); // Brown outline
            g2d.drawRect(195, 130, 10, 50);
        }
        if (wrongGuesses >= 3) {
            // Left arm
            g2d.setColor(new Color(255, 218, 185)); // Peach skin
            g2d.setStroke(new BasicStroke(8));
            g2d.drawLine(200, 140, 165, 160);
        }
        if (wrongGuesses >= 4) {
            // Right arm
            g2d.setColor(new Color(255, 218, 185)); // Peach skin
            g2d.drawLine(200, 140, 235, 160);
        }
        if (wrongGuesses >= 5) {
            // Left leg
            g2d.setColor(new Color(25, 25, 112)); // Dark blue pants
            g2d.setStroke(new BasicStroke(8));
            g2d.drawLine(200, 180, 165, 220);
        }
        if (wrongGuesses >= 6) {
            // Right leg
            g2d.setColor(new Color(25, 25, 112)); // Dark blue pants
            g2d.drawLine(200, 180, 235, 220);
        }
        
        // Add some decorative elements
        g2d.setColor(new Color(0, 191, 255));
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.drawString("Wrong Guesses: " + wrongGuesses + "/6", 20, 30);
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
                    button.setBackground(new Color(76, 175, 80)); // Green for correct
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(76, 175, 80), 3),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                } else {
                    button.setBackground(new Color(244, 67, 54)); // Red for incorrect
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(244, 67, 54), 3),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                }
            } else {
                // Reset to default state for unguessed letters
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
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
        String title = won ? "Congratulations!" : "Game Over!";
        String message = won ? 
            "You won!\n\n" +
            "Score: " + score + "\n" +
            "Level: " + level + "\n" +
            "Diamonds: " + diamonds + "\n" +
            "Trophies: " + trophies + "\n\n" +
            "Would you like to play again?" :
            "Game Over!\n\n" +
            "The word was: " + currentWord + "\n" +
            "Score: " + score + "\n" +
            "Level: " + level + "\n\n" +
            "Would you like to try again?";
        
        // Custom styling for the dialog - White theme
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.foreground", Color.BLACK);
        UIManager.put("Panel.background", Color.WHITE);
        
        int choice = JOptionPane.showConfirmDialog(this, message, title, 
            JOptionPane.YES_NO_OPTION, won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        } else {
            System.exit(0);
        }
    }
    
    private void updateScoreDisplay() {
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + level);
        diamondsLabel.setText("Diamonds: " + diamonds);
        trophiesLabel.setText("Trophies: " + trophies);
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
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
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
            
            HangmanSwingGUI game = new HangmanSwingGUI();
            game.setVisible(true);
        });
    }
}
