package com.example.a2lazy.models;

public class NotificationModel {

    private String notificationMessege, senderUserEmail;

    public NotificationModel() {
    }

    public String getNotificationMessege() {
        return notificationMessege;
    }

    public String getSenderUserEmail() {
        return senderUserEmail;
    }

    public NotificationModel(String notificationMessege, String senderUserEmail) {
        this.notificationMessege = notificationMessege;
        this.senderUserEmail = senderUserEmail;


    }
}
