# Hangman Game Runner Script
# This script compiles and runs the Hangman game using your JavaFX SDK

Write-Host "Starting Hangman Game..." -ForegroundColor Green
Write-Host ""

# Set JavaFX module path
$PATH_TO_FX = "C:\Users\S I F A T\Downloads\openjfx-24.0.2_windows-x64_bin-sdk\javafx-sdk-24.0.2\lib"

# Check if JavaFX path exists
if (-not (Test-Path $PATH_TO_FX)) {
    Write-Host "Error: JavaFX SDK not found at $PATH_TO_FX" -ForegroundColor Red
    Write-Host "Please update the PATH_TO_FX variable in this script" -ForegroundColor Yellow
    Write-Host "to point to your JavaFX installation directory" -ForegroundColor Yellow
    Read-Host "Press Enter to continue"
    exit 1
}

# Check if Java is available
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java version found:" -ForegroundColor Green
    Write-Host $javaVersion[0] -ForegroundColor Cyan
} catch {
    Write-Host "Error: Java not found in PATH" -ForegroundColor Red
    Write-Host "Please install Java 17 or higher and add it to your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to continue"
    exit 1
}

# Check if javac is available
try {
    $javacVersion = javac -version 2>&1
    Write-Host "Java compiler found:" -ForegroundColor Green
    Write-Host $javacVersion[0] -ForegroundColor Cyan
} catch {
    Write-Host "Error: Java compiler (javac) not found in PATH" -ForegroundColor Red
    Write-Host "Please ensure JDK is installed and added to PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to continue"
    exit 1
}

Write-Host ""
Write-Host "Compiling Hangman game..." -ForegroundColor Yellow

# Create target directory if it doesn't exist
if (-not (Test-Path "target\classes")) {
    New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null
}

# Compile the Java files
try {
    javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -d target/classes src/main/java/com/hangman/*.java src/main/java/module-info.java
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Compilation successful!" -ForegroundColor Green
    } else {
        Write-Host "Compilation failed!" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        exit 1
    }
} catch {
    Write-Host "Compilation error: $_" -ForegroundColor Red
    Read-Host "Press Enter to continue"
    exit 1
}

Write-Host ""
Write-Host "Starting Hangman game..." -ForegroundColor Green
Write-Host ""

# Run the game
try {
    java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp target/classes com.hangman.HangmanGame
} catch {
    Write-Host "Error running game: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "Game finished." -ForegroundColor Green
Read-Host "Press Enter to continue"
