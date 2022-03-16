module com.example.platformer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.platformer to javafx.fxml;
    exports com.example.platformer;
}