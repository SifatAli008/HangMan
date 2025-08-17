module com.hangman {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;
    
    opens com.hangman to javafx.fxml;
    exports com.hangman;
}
