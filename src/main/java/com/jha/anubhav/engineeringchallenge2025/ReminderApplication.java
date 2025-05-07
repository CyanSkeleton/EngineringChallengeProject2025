package com.jha.anubhav.engineeringchallenge2025;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.quartz.SchedulerException;

import java.io.IOException;

import static com.jha.anubhav.engineeringchallenge2025.Controller.saveDataToDisk;
import static com.jha.anubhav.engineeringchallenge2025.Controller.scheduler;

public class ReminderApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReminderApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Reminders");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop(){
        try {
            saveDataToDisk();
            scheduler.shutdown();
        } catch (SchedulerException | IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}