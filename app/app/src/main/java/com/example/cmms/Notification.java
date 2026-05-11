package com.example.cmms;

import java.util.Date;

public class Notification {
    private String title;
    private String date;
    private String message;
    private boolean isRead;

    public Notification(String title, String date, String message, Boolean isRead){
        this.title = title;
        this.date = date;
        this.message = message;
        this.isRead = isRead;
    }

    public String getTitle(){
        return title;
    }
    public String getDate(){
        return date;
    }
    public String getMessage(){
        return message;
    }
    public boolean getIsRead(){
        return isRead;
    }
}
