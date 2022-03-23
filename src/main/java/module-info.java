module com.example.platformer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.platformer to javafx.fxml;
    exports com.example.platformer;
}