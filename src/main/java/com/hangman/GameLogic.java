package com.hangman;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GameLogic {
    private static final String[] WORDS = {
        "JAVA", "PYTHON", "JAVAFX", "MAVEN", "THREAD", "PROGRAMMING", "COMPUTER", "ALGORITHM",
        "DATABASE", "NETWORK", "SOFTWARE", "HARDWARE", "INTERFACE", "VARIABLE", "FUNCTION",
        "CLASS", "OBJECT", "METHOD", "CONSTRUCTOR", "INHERITANCE", "POLYMORPHISM", "ENCAPSULATION"
    };
    
    private static final String[] CATEGORIES = {
        "Programming", "Technology", "Computer Science", "Software Development"
    };
    
    private String currentWord;
    private String currentCategory;
    private Set<Character> guessedLetters;
    private int wrongGuesses;
    private int maxWrongGuesses;
    private int score;
    private int level;
    private boolean gameOver;
    private boolean gameWon;
    
    private ExecutorService executorService;
    private Future<?> timerTask;
    private int timeRemaining;
    private static final int TIME_LIMIT = 60; // seconds
    
    public GameLogic() {
        executorService = Executors.newSingleThreadExecutor();
        resetGame();
    }
    
    public void resetGame() {
        Random random = new Random();
        currentWord = WORDS[random.nextInt(WORDS.length)];
        currentCategory = CATEGORIES[random.nextInt(CATEGORIES.length)];
        guessedLetters = new HashSet<>();
        wrongGuesses = 0;
        maxWrongGuesses = 6;
        gameOver = false;
        gameWon = false;
        timeRemaining = TIME_LIMIT;
        startTimer();
    }
    
    private void startTimer() {
        if (timerTask != null && !timerTask.isDone()) {
            timerTask.cancel(true);
        }
        
        timerTask = executorService.submit(() -> {
            try {
                while (timeRemaining > 0 && !gameOver) {
                    Thread.sleep(1000);
                    timeRemaining--;
                    if (timeRemaining <= 0) {
                        gameOver = true;
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    public boolean makeGuess(char letter) {
        if (gameOver || guessedLetters.contains(letter)) {
            return false;
        }
        
        letter = Character.toUpperCase(letter);
        guessedLetters.add(letter);
        
        if (currentWord.indexOf(letter) == -1) {
            wrongGuesses++;
            if (wrongGuesses >= maxWrongGuesses) {
                gameOver = true;
            }
            return false;
        } else {
            checkWinCondition();
            return true;
        }
    }
    
    private void checkWinCondition() {
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                return;
            }
        }
        gameWon = true;
        gameOver = true;
        score += calculateScore();
        level++;
    }
    
    private int calculateScore() {
        int baseScore = 100;
        int timeBonus = timeRemaining * 2;
        int wrongGuessPenalty = wrongGuesses * 10;
        return Math.max(0, baseScore + timeBonus - wrongGuessPenalty);
    }
    
    public String getDisplayWord() {
        StringBuilder display = new StringBuilder();
        for (char c : currentWord.toCharArray()) {
            if (guessedLetters.contains(c)) {
                display.append(c);
            } else {
                display.append("_");
            }
            display.append(" ");
        }
        return display.toString().trim();
    }
    
    public String getCurrentWord() {
        return currentWord;
    }
    
    public String getCurrentCategory() {
        return currentCategory;
    }
    
    public Set<Character> getGuessedLetters() {
        return new HashSet<>(guessedLetters);
    }
    
    public int getWrongGuesses() {
        return wrongGuesses;
    }
    
    public int getMaxWrongGuesses() {
        return maxWrongGuesses;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public boolean isGameWon() {
        return gameWon;
    }
    
    public int getTimeRemaining() {
        return timeRemaining;
    }
    
    public void shutdown() {
        if (timerTask != null && !timerTask.isDone()) {
            timerTask.cancel(true);
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
