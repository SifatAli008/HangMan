@echo off
echo Compiling Hangman Game...
echo.

REM Create target directory if it doesn't exist
if not exist "target\classes" mkdir "target\classes"

REM Compile the Java files
javac -cp "lib/*" -d "target\classes" "src\main\java\com\hangman\HangmanGame.java"

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo Running Hangman Game...
    echo.
    java -cp "target\classes;lib/*" com.hangman.HangmanGame
) else (
    echo Compilation failed! Please check the error messages above.
)

pause
