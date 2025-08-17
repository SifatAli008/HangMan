@echo off
echo Starting Hangman Game...
echo.

REM Set JavaFX module path
set PATH_TO_FX=C:\Users\S I F A T\Downloads\openjfx-24.0.2_windows-x64_bin-sdk\javafx-sdk-24.0.2\lib

REM Check if JavaFX path exists
if not exist "%PATH_TO_FX%" (
    echo Error: JavaFX SDK not found at %PATH_TO_FX%
    echo Please update the PATH_TO_FX variable in this batch file
    echo to point to your JavaFX installation directory
    pause
    exit /b 1
)

REM Check if Maven is available
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven not found. Trying to run with manual JavaFX setup...
    goto manual_run
)

echo Using Maven to run the game...
mvn clean compile
if %errorlevel% neq 0 (
    echo Maven build failed. Trying manual compilation...
    goto manual_run
)

echo Starting game with Maven...
mvn javafx:run
goto end

:manual_run
echo Compiling manually...
if not exist "target\classes" mkdir target\classes

javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -d target/classes src/main/java/com/hangman/*.java src/main/java/module-info.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Starting game...
java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -cp target/classes com.hangman.HangmanGame

:end
echo.
echo Game finished.
pause
