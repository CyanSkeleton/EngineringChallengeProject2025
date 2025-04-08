package com.jha.anubhav.engineeringchallenge2025;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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
    }
}

