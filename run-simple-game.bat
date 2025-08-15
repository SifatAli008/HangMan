@echo off
echo 🎯 Compiling and Running Simple Hangman Game...
echo.

REM Create target directory if it doesn't exist
if not exist "target\classes" mkdir "target\classes"

REM Compile the simple game (no dependencies needed)
echo Compiling SimpleHangmanGame...
javac -d target/classes src/main/java/com/hangman/SimpleHangmanGame.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilation successful! Starting the game...
    echo.
    echo ========================================
    echo 🎮 HANGMAN GAME - SIMPLE VERSION
    echo ========================================
    echo.
    java -cp target/classes com.hangman.SimpleHangmanGame
) else (
    echo.
    echo ❌ Compilation failed! Please check for errors.
    pause
)
