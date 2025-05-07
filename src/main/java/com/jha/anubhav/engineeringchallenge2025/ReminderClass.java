package com.jha.anubhav.engineeringchallenge2025;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.quartz.*;
import java.awt.*;

import static com.jha.anubhav.engineeringchallenge2025.Controller.*;

public class ReminderClass implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        boolean notifications = dataMap.getBoolean("notifications");
        boolean emails = dataMap.getBoolean("emails");

        String title = dataMap.getString("title");
        String description = dataMap.getString("description");

        if(emails)
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("ReminderApp@gmail.com"));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(settings.receiverEmail));
                message.setSubject("Reminder for " + title);
                message.setText("You have a deadline for " + title + " today\n" + description);
                Transport.send(message);
            } catch (MessagingException e) {
                System.out.println(e);
            }
        if(notifications){
            trayIcon.displayMessage(title, description, TrayIcon.MessageType.INFO);
        }
    }
}
