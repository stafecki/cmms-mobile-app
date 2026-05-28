package com.example.cmms.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cmms.data.local.dao.MachineDao;
import com.example.cmms.data.local.dao.NotificationDao;
import com.example.cmms.data.local.dao.WorkOrderDao;
import com.example.cmms.data.local.entities.MachineEntity;
import com.example.cmms.data.local.entities.NotificationEntity;
import com.example.cmms.data.local.entities.WorkOrderEntity;
import com.example.cmms.utils.Constants;

@Database(
        entities = {MachineEntity.class, WorkOrderEntity.class, NotificationEntity.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract MachineDao machineDao();
    public abstract WorkOrderDao workOrderDao();
    public abstract NotificationDao notificationDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            Constants.DB_NAME
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }
}