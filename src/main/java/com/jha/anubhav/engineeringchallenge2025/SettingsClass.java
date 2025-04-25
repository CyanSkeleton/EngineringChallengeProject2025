package com.jha.anubhav.engineeringchallenge2025;

public class SettingsClass {
    public Boolean notifications = true;
    public Boolean emails = true;
    public String senderEmail;
    public String appPassword;
    public String receiverEmail;
    public String reminderTime;

    public SettingsClass(Boolean notifications, Boolean emails,
                         String senderEmail, String appPassword, String receiverEmail, String reminderTime) {
        this.notifications = notifications;
        this.emails = emails;
        this.senderEmail = senderEmail;
        this.appPassword = appPassword;
        this.receiverEmail = receiverEmail;
        this.reminderTime = reminderTime;
    }
}
