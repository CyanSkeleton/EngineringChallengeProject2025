package com.jha.anubhav.engineeringchallenge2025;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

import java.awt.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.List;

import jakarta.mail.*;


import org.quartz.*;
import org.quartz.impl.*;

import com.google.gson.Gson;
import java.io.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;


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
    private Button editNewDeadlineButton;

    @FXML
    private TextField titleField;
    @FXML
    private TextField dateField;
    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField titleField2;
    @FXML
    private TextField dateField2;
    @FXML
    private TextArea descriptionField2;

    @FXML
    private HBox deadlineMenuA;
    @FXML
    private HBox deadlineMenuB;
    @FXML
    private HBox settingsMenu;

    @FXML
    private CheckBox notification;
    @FXML
    private CheckBox email;
    @FXML
    private TextField senderEmail;
    @FXML
    private TextField appPassword;
    @FXML
    private TextField receiverEmail;
    @FXML
    private TextField notificationTime;

    public static SettingsClass settings;

    static Scheduler scheduler;

    static Session session;

    static TrayIcon trayIcon;

    static List<String> deadlineTitles = new ArrayList<>();
    static Map<String, Deadline> deadlineDict = new TreeMap<>();
    
    String currentlySelected;

    public DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/d/uuuu");
    public DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
    public DateTimeFormatter triggerFormat = DateTimeFormatter.ofPattern("0 m H d M ? yyyy");

    public void addDeadline(String title, String date, String description) {
        Deadline deadline = new Deadline(title, date, description);
        deadlineTitles.add(title);
        deadlineDict.put(title, deadline);

        int alrExistingJobs = deadlineDict.size();

        JobDetail job = newJob(ReminderClass.class)
                .withIdentity("job" + Integer.toString(alrExistingJobs),
                        "group" + Integer.toString(alrExistingJobs))
                .usingJobData("notifications", true)
                .usingJobData("emails", true)
                .usingJobData("title", title)
                .usingJobData("date", date)
                .usingJobData("description", description)
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger" + Integer.toString(alrExistingJobs),
                        "group" + Integer.toString(alrExistingJobs))
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 6 * * ?"))
                .forJob("job" + Integer.toString(alrExistingJobs),
                        "group" + Integer.toString(alrExistingJobs))
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        deadlineMenuA.setDisable(true);
        deadlineMenuA.setOpacity(0);
    }

    public void editDeadline(String title, String date, String description) {
        Deadline currentlySelectedDeadline = deadlineDict.get(currentlySelected);
        int index = deadlineTitles.indexOf(currentlySelected);
        try {
            scheduler.deleteJob(jobKey("job" + index,
                    "group" + index));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        deadlineTitles.set(index, title);
        deadlineDict.remove(currentlySelectedDeadline.title);
        Deadline deadlineB = new Deadline(title, date, description);
        deadlineDict.put(title, deadlineB);

        JobDetail job = newJob(ReminderClass.class)
                .withIdentity("job" + Integer.toString(index),
                        "group" + Integer.toString(index))
                .usingJobData("notifications", true)
                .usingJobData("emails", true)
                .usingJobData("title", title)
                .usingJobData("date", date)
                .usingJobData("description", description)
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger" + Integer.toString(index),
                        "group" + Integer.toString(deadlineTitles.indexOf(currentlySelectedDeadline.title) + 1))
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 6 * * ?"))
                .forJob("job" + Integer.toString(index),
                        "group" + Integer.toString(index))
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        deadlineMenuB.setDisable(true);
        deadlineMenuB.setOpacity(0);
        refreshGUI();
    }

    public void deleteDeadline() {
        Deadline currentlySelectedDeadline = deadlineDict.get(currentlySelected);
        int index = deadlineTitles.indexOf(currentlySelected);
        try {
            scheduler.deleteJob(jobKey("job" + index,
                    "group" + index));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        deadlineTitles.remove(index);
        deadlineDict.remove(currentlySelectedDeadline.title);
        refreshGUI();
    }

    public void refreshGUI() {
        deadlinesList.getItems().clear();
        deadlinesList.getItems().addAll(deadlineTitles);
        deadlineTitle.setText("Title");
        deadlineDate.setText("Reminder Date: ");
        deadlineDescription.setText("Description");
    }

    public void saveSettings() {
        settings = new SettingsClass(notification.isSelected(), email.isSelected(), senderEmail.getText(),
                appPassword.getText(), receiverEmail.getText(), notificationTime.getText());
        settingsMenu.setDisable(true);
        settingsMenu.setOpacity(0);
    }

    public static void saveDataToDesk() throws IOException {
        Gson gson = new GsonBuilder()
                .create();
        Writer writer = new FileWriter("settings.json");
        gson.toJson(settings, writer);
        writer.close();
        Writer writer2 = new FileWriter("deadlineTitles.json");
        gson.toJson(deadlineTitles, writer2);
        writer2.close();
        Writer writer3 = new FileWriter("deadlineDict.json");
        gson.toJson(deadlineDict, writer3);
        writer3.close();

    }

    public void readDataFromDesk() throws IOException {
        Gson gson = new GsonBuilder()
                .create();
        Reader reader = new FileReader("settings.json");
        settings = gson.fromJson(reader, SettingsClass.class);
        reader.close();
        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        Reader reader2 = new FileReader("deadlineTitles.json");
        deadlineTitles = gson.fromJson(reader2, listType);
        reader2.close();
        Type mapType = new TypeToken<TreeMap<String, Deadline>>(){}.getType();
        Reader reader3 = new FileReader("deadlineDict.json");
        deadlineDict = gson.fromJson(reader3, mapType);
        reader3.close();


        LocalTime timePart = LocalTime.parse(settings.reminderTime, timeFormat);
        //LocalTime ee = LocalTime.parse("0 0 6 28 4 ? 2025", triggerFormat);
        System.out.println(timePart);

        int i = 1;
        for(Deadline deadline:deadlineDict.values()) {
            JobDetail job = newJob(ReminderClass.class)
                    .withIdentity("job" + Integer.toString(i),
                            "group" + Integer.toString(i))
                    .usingJobData("notifications", true)
                    .usingJobData("emails", true)
                    .usingJobData("title", deadline.title)
                    .usingJobData("date", deadline.date)
                    .usingJobData("description", deadline.description)
                    .build();

            LocalDate datePart = LocalDate.parse(deadline.date, dateFormat);
            LocalDateTime cronTrigDate = LocalDateTime.of(datePart, timePart);
            String cronTrigDate2 = cronTrigDate.format(triggerFormat);
            System.out.println(cronTrigDate2);

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger" + Integer.toString(i),
                            "group" + Integer.toString(i))
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronTrigDate2))
                    .forJob("job" + Integer.toString(i),
                            "group" + Integer.toString(i))
                    .build();
            try {
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException e) {
                System.out.println(e);
            }
            i++;
        }
    }

    public void OpenDeadlineMenuA() {
        deadlineMenuA.setDisable(false);
        deadlineMenuA.setOpacity(100);
    }

    public void OpenDeadlineMenuB() {
        deadlineMenuB.setDisable(false);
        deadlineMenuB.setOpacity(100);
    }

    public void OpenSettingMenu() {
        notification.setSelected(settings.notifications);
        email.setSelected(settings.emails);
        senderEmail.setText(settings.senderEmail);
        appPassword.setText(settings.appPassword);
        receiverEmail.setText(settings.receiverEmail);
        notificationTime.setText(settings.reminderTime);
        settingsMenu.setDisable(false);
        settingsMenu.setOpacity(100);
    }

    public void CloseDeadlineMenuA() {
        deadlineMenuA.setDisable(true);
        deadlineMenuA.setOpacity(0);
    }

    public void CloseDeadlineMenuB() {
        deadlineMenuB.setDisable(true);
        deadlineMenuB.setOpacity(0);
    }

    public void CloseSettingMenu() {
        settingsMenu.setDisable(true);
        settingsMenu.setOpacity(0);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        deadlineMenuA.setDisable(true);
        deadlineMenuA.setOpacity(0);
        deadlineMenuB.setDisable(true);
        deadlineMenuB.setOpacity(0);
        settingsMenu.setDisable(true);
        settingsMenu.setOpacity(0);

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        try {
            readDataFromDesk();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");


        session = Session.getInstance(prop, new jakarta.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(settings.senderEmail, settings.appPassword);
            }
        });

        SystemTray sysTray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setToolTip("System tray icon demo");
        try {
            sysTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("This is NOT GOOD.");
            throw new RuntimeException(e);
        }

        refreshGUI();

        deadlinesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                currentlySelected = deadlinesList.getSelectionModel().getSelectedItem();
                deadlineTitle.setText(currentlySelected);
                try {
                    deadlineDate.setText("Reminder Date: " + deadlineDict.get(currentlySelected).date);
                    deadlineDescription.setText(deadlineDict.get(currentlySelected).description);
                } catch (Exception e) {
                    System.out.println("expected error that occurs because size of dict has changed");
                }
            }
        });
        createNewDeadlineButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addDeadline(titleField.getText(), dateField.getText(), descriptionField.getText());
                refreshGUI();
            }
        });
        editNewDeadlineButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("e");
                editDeadline(titleField2.getText(), dateField2.getText(), descriptionField2.getText());
                refreshGUI();
            }
        });

    }
}

