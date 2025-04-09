package com.jha.anubhav.engineeringchallenge2025;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.util.*;

import java.net.URL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Controller implements Initializable {

    @FXML
    private ListView<String> deadlinesList;

    @FXML
    private Label deadlineTitle;

    @FXML
    private Label deadlineDate;

    @FXML
    private Label deadlineDescription;

    @FXML
    private Button createNewDeadlineButton;

    @FXML
    private TextField titleField;

    @FXML
    private TextField dateField;

    @FXML
    private TextArea descriptionField;

    Deadline deadline1 = new Deadline("Project1", "4/10/2025", "testing");
    Deadline deadline2 = new Deadline("Project2", "6/7/2025", "test");
    Deadline deadline3 = new Deadline("Project3", "5/4/2025", "tests");
    
    String currentlySelected;

    public Map<LocalDate, Deadline> deadlineDict = new TreeMap<>();
    public Map<String, Deadline> deadlineDict2 = new TreeMap<>();

    public DateTimeFormatter standardFormat = DateTimeFormatter.ofPattern("M/d/uuuu");

    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        // This doesn't work when I try moving it to the css file
        //titleField.setStyle("-fx-control-inner-background: #1C444A; -fx-text-fill:  #A9EAEF; -fx-prompt-text-fill: #5CB5CD;");
        //dateField.setStyle("-fx-control-inner-background: #1C444A; -fx-text-fill:  #A9EAEF; -fx-prompt-text-fill: #5CB5CD;");
        //descriptionField.setStyle("-fx-control-inner-background: #1C444A;  " +
        //        " -fx-focus-color: transparent; -fx-border-style: none; -fx-background-radius: 0.0px;");

        deadlineDict.put(LocalDate.parse(deadline1.date, standardFormat), deadline1);
        deadlineDict.put(LocalDate.parse(deadline2.date, standardFormat), deadline2);
        deadlineDict.put(LocalDate.parse(deadline3.date, standardFormat), deadline3);

        List<String> deadlineTitles = new ArrayList<>();
        for(LocalDate key : deadlineDict.keySet()) {
            deadlineTitles.add(deadlineDict.get(key).title);
            deadlineDict2.put(deadlineDict.get(key).title, deadlineDict.get(key));
        }
        deadlinesList.getItems().addAll(deadlineTitles);

        deadlinesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                currentlySelected = deadlinesList.getSelectionModel().getSelectedItem();
                deadlineTitle.setText(currentlySelected);
                deadlineDate.setText("Due Date: " + deadlineDict2.get(currentlySelected).date);
                deadlineDescription.setText(deadlineDict2.get(currentlySelected).description);
            }
        });
        createNewDeadlineButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                
            }
        });

    }
}

