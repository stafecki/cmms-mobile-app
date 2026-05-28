package com.example.cmms.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cmms.data.local.entities.NotificationEntity;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NotificationEntity> notifications);

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    LiveData<List<NotificationEntity>> getAll();

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    LiveData<Integer> getUnreadCount();

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    void markAsRead(String id);

    @Query("DELETE FROM notifications")
    void deleteAll();
}