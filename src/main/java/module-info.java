module com.hangman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    
    opens com.hangman to javafx.fxml;
    exports com.hangman;
}
