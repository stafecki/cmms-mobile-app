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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WorkOrderEntity workOrder);

    @Query("UPDATE work_orders SET status = :status WHERE id = :id")
    void updateStatus(String id, String status);

    @Query("SELECT * FROM work_orders ORDER BY createdAt DESC")
    LiveData<List<WorkOrderEntity>> getAll();

    @Query("SELECT * FROM work_orders WHERE id = :id")
    LiveData<WorkOrderEntity> getById(String id);

    @Query("SELECT * FROM work_orders WHERE status = :status ORDER BY createdAt DESC")
    LiveData<List<WorkOrderEntity>> getByStatus(String status);

    @Query("SELECT * FROM work_orders WHERE assignedToId = :userId ORDER BY createdAt DESC")
    LiveData<List<WorkOrderEntity>> getByAssignedTo(String userId);

    @Query("SELECT * FROM work_orders WHERE assignedToId = :userId AND status = :status ORDER BY createdAt DESC")
    LiveData<List<WorkOrderEntity>> getByAssignedToAndStatus(String userId, String status);

    @Query("SELECT * FROM work_orders WHERE reportedById = :userId ORDER BY createdAt DESC")
    LiveData<List<WorkOrderEntity>> getByReportedBy(String userId);

    @Query("SELECT * FROM work_orders WHERE reportedById = :userId AND status = :status ORDER BY createdAt DESC")
    LiveData<List<WorkOrderEntity>> getByReportedByAndStatus(String userId, String status);

    @Query("DELETE FROM work_orders")
    void deleteAll();
}