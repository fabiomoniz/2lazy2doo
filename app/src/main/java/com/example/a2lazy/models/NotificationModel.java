package com.example.a2lazy.models;

public class NotificationModel {

    private String notificationMessage, senderUserEmail;

    public NotificationModel() {
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public String getSenderUserEmail() {
        return senderUserEmail;
    }

    public NotificationModel(String notificationMessege, String senderUserEmail) {
        this.notificationMessage = notificationMessege;
        this.senderUserEmail = senderUserEmail;


    }
}
