@echo off
echo Building Hangman Game...

REM Create target directory if it doesn't exist
if not exist "target\classes" mkdir "target\classes"

REM Compile all Java files
echo Compiling Java files...
javac -d target/classes src/main/java/com/hangman/**/*.java

if %ERRORLEVEL% EQU 0 (
    echo Build successful!
    echo.
    echo To run the game, use one of these commands:
echo   java -cp target/classes com.hangman.HangmanGameLauncher
echo   java -cp target/classes com.hangman.gui.JavaFXHangmanGame
echo.
echo Or simply run: java -cp target/classes com.hangman.HangmanGameLauncher
    echo.
    pause
) else (
    echo Build failed! Please check for compilation errors.
    pause
)
