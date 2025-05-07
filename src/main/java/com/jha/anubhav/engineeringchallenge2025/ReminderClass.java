package com.jha.anubhav.engineeringchallenge2025;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.quartz.*;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.jha.anubhav.engineeringchallenge2025.Controller.*;

public class ReminderClass implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        Boolean notifications = dataMap.getBoolean("notifications");
        Boolean emails = dataMap.getBoolean("emails");

        DateTimeFormatter standardFormat = DateTimeFormatter.ofPattern("M/d/uuuu");

        String title = dataMap.getString("title");
        String date = dataMap.getString("date");
        String description = dataMap.getString("description");

        System.out.println(date);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("ReminderApp@gmail.com"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(settings.receiverEmail));
            message.setSubject("Reminder for " + title);
            message.setText("You have a deadline for " + title + " today\n" + description);
            Transport.send(message);
        } catch (MessagingException e) {
            System.out.println(e.toString());
        }

        trayIcon.displayMessage(title, description, TrayIcon.MessageType.INFO);
    }
}
