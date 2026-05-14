package com.example.cmms.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.cmms.data.local.AppDatabase;
import com.example.cmms.data.local.dao.WorkOrderDao;
import com.example.cmms.data.local.entities.WorkOrderEntity;
import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.WorkOrderResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkOrderRepository {

    private static final String TAG = "WorkOrderRepository";

    private final AppDatabase database;
    private final WorkOrderDao workOrderDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public WorkOrderRepository(@NonNull Context context) {
        this.database = AppDatabase.getInstance(context);
        this.workOrderDao = database.workOrderDao();
    }

    public LiveData<List<WorkOrderEntity>> getWorkOrders(@NonNull String token) {
        ApiService api = ApiClient.getApiService(token);
        api.getWorkOrders().enqueue(new Callback<List<WorkOrderResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<WorkOrderResponse>> call, @NonNull Response<List<WorkOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkOrderEntity> entities = mapWorkOrders(response.body());
                    executor.execute(() -> database.runInTransaction(() -> {
                        workOrderDao.deleteAll();
                        workOrderDao.insertAll(entities);
                    }));
                    Log.d(TAG, "Fetched " + entities.size() + " work orders from API");
                } else {
                    Log.e(TAG, "getWorkOrders failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WorkOrderResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "getWorkOrders network error", t);
            }
        });
        return workOrderDao.getAll();
    }

    public LiveData<WorkOrderEntity> getWorkOrderById(@NonNull String id, @NonNull String token) {
        ApiService api = ApiClient.getApiService(token);
        api.getWorkOrderById(id).enqueue(new Callback<WorkOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkOrderResponse> call, @NonNull Response<WorkOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkOrderEntity entity = mapWorkOrder(response.body());
                    executor.execute(() -> workOrderDao.insertAll(Collections.singletonList(entity)));
                    Log.d(TAG, "Fetched work order: " + id);
                } else {
                    Log.e(TAG, "getWorkOrderById failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkOrderResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getWorkOrderById network error", t);
            }
        });
        return workOrderDao.getById(id);
    }

    public LiveData<List<WorkOrderEntity>> getWorkOrdersByStatus(@NonNull String status) {
        return workOrderDao.getByStatus(status);
    }

    private List<WorkOrderEntity> mapWorkOrders(List<WorkOrderResponse> responses) {
        List<WorkOrderEntity> entities = new ArrayList<>(responses.size());
        for (WorkOrderResponse r : responses) {
            entities.add(mapWorkOrder(r));
        }
        return entities;
    }

    private WorkOrderEntity mapWorkOrder(WorkOrderResponse r) {
        return new WorkOrderEntity(
                r.getId(),
                r.getTitle(),
                r.getStatus(),
                r.getPriority(),
                r.getDescription(),
                r.isBhpConfirmed(),
                r.getCreatedAt()
        );
    }
}
