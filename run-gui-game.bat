@echo off
echo 🎯 Compiling and Running Hangman Game with GUI...
echo.

REM Create target directory if it doesn't exist
if not exist "target\classes" mkdir "target\classes"

REM Compile the core engine first
echo Compiling core engine...
javac -d target/classes src/main/java/com/hangman/core/HangmanGameEngine.java

REM Compile the GUI
echo Compiling Swing GUI...
javac -d target/classes src/main/java/com/hangman/gui/SwingHangmanGUI.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilation successful! Starting the GUI game...
    echo.
    echo ========================================
    echo 🎮 HANGMAN GAME - SWING GUI VERSION
    echo ========================================
    echo.
    java -cp target/classes com.hangman.gui.SwingHangmanGUI
) else (
    echo.
    echo ❌ Compilation failed! Please check for errors.
    pause
)
