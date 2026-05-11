package com.example.cmms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<List<Notification>> notifications;
    private MutableLiveData<Integer> unreadNotificationsCount;

    public LiveData<List<Notification>> getNotifications() {
        if(notifications == null) {
            notifications = new MutableLiveData<>();
            loadNotifications();
        }
        return notifications;
    }

    public LiveData<Integer> getUnreadNotificationsCount() {
        if(unreadNotificationsCount == null) {
            unreadNotificationsCount = new MutableLiveData<>();
            loadUnreadNotificationsCount();
        }
        return unreadNotificationsCount;
    }


    public void refreshNotifications(){
        loadNotifications();
    }

    public void refreshUnreadNotificationsCount(){
        loadUnreadNotificationsCount();
    }

    private void loadNotifications() {
        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(new Notification("Tytul 1", "2026-05-08 14:30","Treść 1", true));
        notificationList.add(new Notification("Tytul 2", "2026-05-07 12:14", "Treść 2", false));
        notificationList.add(new Notification("Tytul 3", "2026-05-07 12:25"  , "Treść 3", true));
        notificationList.add(new Notification("Tytul 4", "2026-05-06 11:33", "Treść 4", false));
        notifications.setValue(notificationList);
    }

    private void loadUnreadNotificationsCount() {
        int count = 0;
        List<Notification> notificationList = notifications.getValue();
        if(notificationList != null) {
            for (Notification notification : notificationList) {
                if (!notification.getIsRead()) {
                    count++;
                }
            }
        }
        unreadNotificationsCount.setValue(count);
    }


}
