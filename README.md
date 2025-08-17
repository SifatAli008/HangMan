# Hangman Game

A JavaFX-based Hangman game built with Java, Maven, and threading capabilities.

## Features

- Interactive GUI with JavaFX
- On-screen keyboard and physical keyboard support
- Timer-based gameplay with countdown
- Score tracking and level progression
- Hint system
- Professional dark theme UI
- Threading for timer management
- Maven build system

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- JavaFX SDK 24.0.2

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── com/
│   │   │   └── hangman/
│   │   │       ├── HangmanGame.java      # Main application class
│   │   │       ├── GameLogic.java        # Game logic and threading
│   │   │       └── HangmanController.java # UI controller
│   │   └── module-info.java              # Java module configuration
│   └── resources/
│       └── hangman.fxml                  # UI layout file
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

## Building and Running

### Option 1: Using Maven (Recommended)

1. **Build the project:**
   ```bash
   mvn clean compile
   ```

2. **Run the game:**
   ```bash
   mvn javafx:run
   ```

3. **Package the application:**
   ```bash
   mvn clean package
   ```

4. **Run the packaged JAR:**
   ```bash
   java -jar target/hangman-game-1.0.0.jar
   ```

### Option 2: Using IDE

1. Import the project into your IDE (IntelliJ IDEA, Eclipse, NetBeans)
2. Ensure JavaFX SDK is configured in your IDE
3. Run the `HangmanGame` class

### Option 3: Manual JavaFX Setup

If you need to manually configure JavaFX:

1. **Set JavaFX module path:**
   ```bash
   # Windows
   set PATH_TO_FX="C:\Users\S I F A T\Downloads\openjfx-24.0.2_windows-x64_bin-sdk\javafx-sdk-24.0.2\lib"
   
   # Compile
   javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -d target/classes src/main/java/com/hangman/*.java src/main/java/module-info.java
   
   # Run
   java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp target/classes com.hangman.HangmanGame
   ```

## Game Rules

1. **Objective:** Guess the hidden word before the hangman is fully drawn
2. **Guesses:** You have 6 wrong guesses allowed
3. **Timer:** Each round has a 60-second time limit
4. **Scoring:** Points based on time remaining and wrong guesses
5. **Levels:** Progress through levels by winning games
6. **Hints:** Use hints to get help when stuck

## Controls

- **Mouse:** Click on on-screen keyboard buttons
- **Keyboard:** Type letters to make guesses
- **New Game:** Click "New Game" button to start over
- **Hint:** Click "Hint" button for help

## Technical Details

### Threading
- Timer management using `ExecutorService`
- Background countdown timer
- Thread-safe game state updates

### JavaFX Features
- FXML-based UI layout
- Canvas for hangman drawing
- Responsive grid-based keyboard
- Event handling for user interactions

### Maven Configuration
- JavaFX Maven plugin for easy execution
- Shade plugin for creating executable JAR
- Proper dependency management

## Troubleshooting

### Common Issues

1. **JavaFX not found:**
   - Ensure JavaFX SDK is properly installed
   - Check module path configuration
   - Verify Java version compatibility

2. **Maven build errors:**
   - Clean and rebuild: `mvn clean compile`
   - Check Java version: `java -version`
   - Verify Maven version: `mvn -version`

3. **Runtime errors:**
   - Check console for error messages
   - Ensure all dependencies are resolved
   - Verify file paths and permissions

### Performance Tips

- Close other applications for better performance
- Ensure adequate system memory
- Use hardware acceleration if available

## Contributing

Feel free to contribute improvements:
- Add new word categories
- Enhance UI design
- Implement additional features
- Optimize performance

## License

This project is open source and available under the MIT License.

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review console error messages
3. Verify system requirements
4. Check JavaFX installation

Enjoy playing Hangman! :-)
