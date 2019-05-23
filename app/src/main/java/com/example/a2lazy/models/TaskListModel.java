package com.example.a2lazy.models;

import java.io.Serializable;
import java.util.Date;
import com.google.firebase.firestore.ServerTimestamp;

public class TaskListModel implements Serializable {
    private String taskListId, taskListName, createdBy, imageUrl;
    @ServerTimestamp
    private Date date;

    public TaskListModel() {}

    public TaskListModel(String shoppingListId, String shoppingListName, String createdBy, String imageUrl) {
        this.taskListId = shoppingListId;
        this.taskListName = shoppingListName;
        this.createdBy = createdBy;
        this.imageUrl = imageUrl;
    }

    public String getTaskListId() {
        return taskListId;
    }

    public String getTaskListName() {
        return taskListName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
