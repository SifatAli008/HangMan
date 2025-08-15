# Hangman Game

A modern Hangman game built with Java, JavaFX, and threads featuring a beautiful UI with categories, scoring system, and interactive gameplay.

## Features

- **Multiple Categories**: Mythology, Animals, Countries, Food, and Sports
- **Interactive Keyboard**: Click letters to guess, visual feedback for correct/incorrect guesses
- **Visual Hangman**: Canvas-based drawing that builds as you make wrong guesses
- **Scoring System**: Earn points, diamonds, and trophies as you progress
- **Hint System**: Use diamonds to get hints when stuck
- **Pause Functionality**: Pause and resume the game at any time
- **Level Progression**: Advance through levels as you win
- **Threading**: Uses Java threads for smooth gameplay and UI responsiveness

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 17 (included in dependencies)

## How to Run

### Option 1: Using Maven (Recommended)

1. **Clone or download the project**
2. **Open terminal/command prompt in the project directory**
3. **Run the game:**
   ```bash
   mvn clean javafx:run
   ```

### Option 2: Using IDE

1. **Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code)**
2. **Ensure Maven dependencies are downloaded**
3. **Run the `HangmanGame.java` file**

### Option 3: Manual Compilation

1. **Compile the project:**
   ```bash
   mvn clean compile
   ```
2. **Run the compiled JAR:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.hangman.HangmanGame"
   ```

## Game Rules

1. **Objective**: Guess the hidden word before the hangman is fully drawn
2. **Guessing**: Click on keyboard letters to make guesses
3. **Correct Guesses**: Reveal letters in the word
4. **Wrong Guesses**: Add parts to the hangman (6 wrong guesses = game over)
5. **Hints**: Spend 10 diamonds to reveal a letter
6. **Scoring**: 
   - Win: +100 points, +1 level, +20 diamonds, +1 trophy
   - Lose: Game over, can restart

## Controls

- **Letter Buttons**: Click to guess letters
- **Hint Button**: Use diamonds to get help
- **Pause Button**: Pause/resume the game
- **New Game**: Start over after winning or losing

## Project Structure

```
HangMan/
├── src/main/java/com/hangman/
│   └── HangmanGame.java          # Main game class
├── pom.xml                       # Maven configuration
└── README.md                     # This file
```

## Technical Details

- **JavaFX**: Modern UI framework for desktop applications
- **Threading**: Uses `ExecutorService` for game logic and `ScheduledExecutorService` for timers
- **Canvas Graphics**: Custom drawing for the hangman visualization
- **Event Handling**: Responsive UI with proper event management
- **Maven**: Dependency management and build automation

## Customization

You can easily customize the game by:
- Adding new categories and words in the `WORDS` array
- Modifying the scoring system
- Changing colors and styling in the UI
- Adding sound effects or animations
- Implementing a high score system

## Troubleshooting

- **JavaFX not found**: Ensure you have Java 17+ and the project dependencies are properly downloaded
- **Compilation errors**: Check that your Java version matches the project requirements
- **Runtime errors**: Verify all Maven dependencies are resolved

## License

This project is open source and available under the MIT License.

Enjoy playing Hangman! :-)
