package com.example.cmms.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cmms.data.local.entities.WorkOrderEntity;

import java.util.List;

@Dao
public interface WorkOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WorkOrderEntity> workOrders);

    @Query("SELECT * FROM work_orders ORDER BY createdAt DESC")
    LiveData<List<WorkOrderEntity>> getAll();

    @Query("SELECT * FROM work_orders WHERE id = :id")
    LiveData<WorkOrderEntity> getById(String id);

    @Query("SELECT * FROM work_orders WHERE status = :status ORDER BY createdAt DESC")
    LiveData<List<WorkOrderEntity>> getByStatus(String status);

    @Query("DELETE FROM work_orders")
    void deleteAll();
}