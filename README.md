# Hangman Game

A modern Java implementation of the classic Hangman word guessing game using JavaFX for a beautiful, modern interface.

## Features

- **Modern JavaFX Interface**: Beautiful, responsive UI with smooth animations
- **Smart Game Engine**: Core game logic separated from UI for maintainability
- **Rich Gameplay**: Categories, scoring system, hints, and progression
- **Professional Design**: Clean, modern interface with intuitive controls

## Project Structure

```
src/main/java/com/hangman/
├── core/
│   └── HangmanGameEngine.java          # Core game logic engine
├── gui/
│   └── JavaFXHangmanGame.java          # JavaFX-based GUI implementation
└── HangmanGameLauncher.java            # Main launcher
```

## How to Run

### Option 1: Launcher (Recommended)
```bash
java -cp target/classes com.hangman.HangmanGameLauncher
```
This will launch the JavaFX version directly.

### Option 2: Direct Launch
```bash
# JavaFX version
java -cp target/classes com.hangman.gui.JavaFXHangmanGame
```

## Building the Project

```bash
# Compile
javac -d target/classes src/main/java/com/hangman/**/*.java

# Run
java -cp target/classes com.hangman.HangmanGameLauncher
```

## Game Features

- **Categories**: Mythology, Animals, Countries, Food, Sports
- **Scoring System**: Points, levels, diamonds, and trophies
- **Hints**: Use diamonds to reveal letters (costs 10 diamonds)
- **Progressive Difficulty**: Levels increase with each win
- **Visual Feedback**: Hangman drawing and keyboard highlighting

## Requirements

- **Java 11+** (JavaFX is included in Java 11+)
- **Maven** (optional, for dependency management)

## Architecture

The project follows a clean architecture pattern:

- **Core Engine** (`HangmanGameEngine`): Contains all game logic, state management, and business rules
- **GUI Layer** (`gui` package): JavaFX-based user interface
- **Launcher** (`HangmanGameLauncher`): Simple entry point that launches the JavaFX game

This separation allows for:
- Easy testing of game logic
- Clean separation between business logic and UI
- Maintainable and extensible codebase

## Contributing

Feel free to contribute by:
- Adding new word categories
- Improving the UI design
- Adding new game features
- Optimizing the game engine

## License

This project is open source and available under the [LICENSE](LICENSE) file.
