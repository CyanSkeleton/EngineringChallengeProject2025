module com.jha.anubhav.engineeringchallenge2025 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jha.anubhav.engineeringchallenge2025 to javafx.fxml;
    exports com.jha.anubhav.engineeringchallenge2025;
}