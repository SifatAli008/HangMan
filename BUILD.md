# 🎯 Hangman Game - Build Guide

This guide will walk you through building and running the Hangman game step by step.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software
- **Java Development Kit (JDK) 17 or higher**
- **Maven 3.6 or higher** (optional, for Maven build)

### Verify Installation
```bash
# Check Java version
java -version

# Check Maven version (if using Maven)
mvn -version
```

## 🚀 Method 1: Direct Java Compilation (Recommended)

### Step 1: Clone the Repository
```bash
git clone https://github.com/SifatAli008/HangMan.git
cd HangMan
```

### Step 2: Create Build Directory
```bash
# Create target directory for compiled classes
mkdir -p target/classes
```

### Step 3: Compile the Game
```bash
# Compile the Swing version (no external dependencies)
javac -d target/classes src/main/java/com/hangman/SimpleHangmanGame.java

# Or compile the JavaFX version (requires JavaFX modules)
javac -d target/classes src/main/java/com/hangman/HangmanGame.java
```

### Step 4: Run the Game
```bash
# Run the Swing version
java -cp target/classes com.hangman.SimpleHangmanGame

# Run the JavaFX version (requires JavaFX modules)
java -cp target/classes com.hangman.HangmanGame
```

## 🏗️ Method 2: Maven Build

### Step 1: Navigate to Project Directory
```bash
cd HangMan
```

### Step 2: Clean and Compile
```bash
# Clean previous builds
mvn clean

# Compile the project
mvn compile
```

### Step 3: Run with Maven
```bash
# Run the JavaFX version
mvn javafx:run

# Or run the Swing version
mvn exec:java -Dexec.mainClass="com.hangman.SimpleHangmanGame"
```

## 🎮 Method 3: Using Provided Scripts

### Windows Users
```bash
# Run the Swing version
run-swing-game.bat

# Run with Maven (if available)
run-game.bat

# Compile and run manually
compile-and-run.bat
```

### Unix/Linux/Mac Users
```bash
# Make script executable
chmod +x run-game.sh

# Run the game
./run-game.sh
```

## 🔧 Troubleshooting

### Common Issues and Solutions

#### Issue 1: "javac is not recognized"
**Solution**: Add Java to your PATH environment variable
```bash
# Windows: Add to PATH
set PATH=%PATH%;C:\Program Files\Java\jdk-17\bin

# Unix/Linux/Mac: Add to PATH
export PATH=$PATH:/usr/lib/jvm/java-17-openjdk/bin
```

#### Issue 2: "java.lang.ClassNotFoundException"
**Solution**: Ensure you're in the correct directory and compiled successfully
```bash
# Check if classes exist
ls target/classes/com/hangman/

# Recompile if needed
javac -d target/classes src/main/java/com/hangman/SimpleHangmanGame.java
```

#### Issue 3: "Error: JavaFX runtime components are missing"
**Solution**: Use the Swing version instead, or install JavaFX modules
```bash
# Run Swing version (recommended)
java -cp target/classes com.hangman.SimpleHangmanGame
```

#### Issue 4: "Permission denied" on Unix/Linux
**Solution**: Make scripts executable
```bash
chmod +x *.sh
```

## 📁 Project Structure

```
HangMan/
├── src/main/java/com/hangman/
│   ├── HangmanGame.java          # JavaFX version
│   └── SimpleHangmanGame.java    # Swing version (recommended)
├── target/classes/                # Compiled classes
├── pom.xml                       # Maven configuration
├── README.md                     # Project documentation
├── BUILD.md                      # This build guide
├── LICENSE                       # MIT License
├── .gitignore                    # Git ignore rules
├── run-game.bat                  # Windows Maven runner
├── run-game.sh                   # Unix Maven runner
├── run-swing-game.bat            # Windows Swing runner
└── compile-and-run.bat           # Windows manual compiler
```

## 🎯 Quick Start (5 Steps)

1. **Clone**: `git clone https://github.com/SifatAli008/HangMan.git`
2. **Navigate**: `cd HangMan`
3. **Compile**: `javac -d target/classes src/main/java/com/hangman/SimpleHangmanGame.java`
4. **Run**: `java -cp target/classes com.hangman.SimpleHangmanGame`
5. **Play**: Enjoy the game! 🎮

## 🌟 Features to Test

After successful build, verify these features work:

- ✅ **Game Window Opens** - Main game interface displays
- ✅ **Keyboard Layout** - All 26 letters visible in grid format
- ✅ **Category Selection** - 5 categories available
- ✅ **Word Guessing** - Click letters to make guesses
- ✅ **Hangman Drawing** - Visual feedback for wrong guesses
- ✅ **Scoring System** - Points, diamonds, and trophies
- ✅ **Hint System** - Use diamonds to reveal letters
- ✅ **Pause Function** - Pause/resume gameplay

## 📞 Support

If you encounter issues:

1. **Check Java Version**: Ensure JDK 17+ is installed
2. **Verify Path**: Ensure Java is in your system PATH
3. **Recompile**: Clean and recompile if needed
4. **Use Swing Version**: `SimpleHangmanGame.java` has no external dependencies

## 🎉 Success!

Once the game runs successfully, you'll see:
- A window titled "Hangman Game"
- Category selection (Mythology, Animals, Countries, Food, Sports)
- Interactive QWERTY keyboard
- Score display with diamonds and trophies
- Hangman drawing area

Happy gaming! 🎯🎮
