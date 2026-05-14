package com.example.cmms.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.cmms.data.local.AppDatabase;
import com.example.cmms.data.local.dao.MachineDao;
import com.example.cmms.data.local.entities.MachineEntity;
import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.MachineResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MachineRepository {

    private static final String TAG = "MachineRepository";

    private final AppDatabase database;
    private final MachineDao machineDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public MachineRepository(@NonNull Context context) {
        this.database = AppDatabase.getInstance(context);
        this.machineDao = database.machineDao();
    }

    public LiveData<List<MachineEntity>> getMachines(@NonNull String token) {
        ApiService api = ApiClient.getApiService(token);
        api.getMachines().enqueue(new Callback<List<MachineResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<MachineResponse>> call, @NonNull Response<List<MachineResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MachineEntity> entities = mapMachines(response.body());
                    executor.execute(() -> database.runInTransaction(() -> {
                        machineDao.deleteAll();
                        machineDao.insertAll(entities);
                    }));
                    Log.d(TAG, "Fetched " + entities.size() + " machines from API");
                } else {
                    Log.e(TAG, "getMachines failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "getMachines network error", t);
            }
        });
        return machineDao.getAll();
    }

    public LiveData<MachineEntity> getMachineById(@NonNull String id, @NonNull String token) {
        ApiService api = ApiClient.getApiService(token);
        api.getMachineById(id).enqueue(new Callback<MachineResponse>() {
            @Override
            public void onResponse(@NonNull Call<MachineResponse> call, @NonNull Response<MachineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MachineEntity entity = mapMachine(response.body());
                    executor.execute(() -> machineDao.insertAll(Collections.singletonList(entity)));
                    Log.d(TAG, "Fetched machine: " + id);
                } else {
                    Log.e(TAG, "getMachineById failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MachineResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getMachineById network error", t);
            }
        });
        return machineDao.getById(id);
    }

    public LiveData<List<MachineEntity>> searchMachines(@NonNull String query) {
        return machineDao.search(query);
    }

    private List<MachineEntity> mapMachines(List<MachineResponse> responses) {
        List<MachineEntity> entities = new ArrayList<>(responses.size());
        for (MachineResponse r : responses) {
            entities.add(mapMachine(r));
        }
        return entities;
    }

    private MachineEntity mapMachine(MachineResponse r) {
        return new MachineEntity(
                r.getId(),
                r.getName(),
                r.getSerialNumber(),
                r.getOperatingHours(),
                r.isActive()
        );
    }
}
