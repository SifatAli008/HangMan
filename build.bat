@echo off
echo Building Hangman Game...
echo.

REM Set JavaFX module path
set PATH_TO_FX=C:\Users\S I F A T\Downloads\openjfx-24.0.2_windows-x64_bin-sdk\javafx-sdk-24.0.2\lib

REM Check if JavaFX path exists
if not exist "%PATH_TO_FX%" (
    echo Error: JavaFX SDK not found at %PATH_TO_FX%
    echo Please update the PATH_TO_FX variable in this batch file
    pause
    exit /b 1
)

REM Create target directory
if not exist "target\classes" mkdir target\classes

echo Compiling Java files...
javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -d target/classes src/main/java/com/hangman/*.java src/main/java/module-info.java

if %errorlevel% equ 0 (
    echo Build successful!
    echo.
    echo To run the game, use: run-game.bat
) else (
    echo Build failed!
)

pause
