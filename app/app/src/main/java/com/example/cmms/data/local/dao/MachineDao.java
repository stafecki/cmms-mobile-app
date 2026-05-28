package com.example.cmms.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cmms.data.local.entities.MachineEntity;

import java.util.List;

@Dao
public interface MachineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MachineEntity> machines);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MachineEntity machine);

    @Query("SELECT * FROM machines WHERE isActive = 1 ORDER BY name ASC")
    LiveData<List<MachineEntity>> getAll();

    @Query("SELECT * FROM machines WHERE id = :id")
    LiveData<MachineEntity> getById(String id);

    @Query("SELECT * FROM machines WHERE name LIKE '%' || :query || '%'")
    LiveData<List<MachineEntity>> search(String query);

    @Query("DELETE FROM machines WHERE id = :id")
    void deleteById(String id);

    @Query("DELETE FROM machines")
    void deleteAll();
}