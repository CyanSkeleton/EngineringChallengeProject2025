module com.jha.anubhav.engineeringchallenge2025 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.mail;
    requires java.desktop;
    requires quartz;
    requires jdk.jfr;
    requires com.google.gson;


    opens com.jha.anubhav.engineeringchallenge2025 to javafx.fxml;
    exports com.jha.anubhav.engineeringchallenge2025;
}