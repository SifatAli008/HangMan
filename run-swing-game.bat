@echo off
echo Compiling Swing Hangman Game...
echo.

REM Create target directory if it doesn't exist
if not exist "target\classes" mkdir "target\classes"

REM Compile the Java files
javac -d "target\classes" "src\main\java\com\hangman\SimpleHangmanGame.java"

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo Running Hangman Game...
    echo.
    java -cp "target\classes" com.hangman.SimpleHangmanGame
) else (
    echo Compilation failed! Please check the error messages above.
)

pause
