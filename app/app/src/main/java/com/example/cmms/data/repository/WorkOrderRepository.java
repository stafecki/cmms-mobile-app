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
import com.example.cmms.data.remote.models.CreateWorkOrderRequest;
import com.example.cmms.data.remote.models.UpdateWorkOrderRequest;
import com.example.cmms.data.remote.models.UpdateWorkOrderStatusRequest;
import com.example.cmms.data.remote.models.WorkOrderResponse;
import com.example.cmms.utils.NetworkUtils;

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

    private final Context context;
    private final AppDatabase database;
    private final WorkOrderDao workOrderDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public WorkOrderRepository(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(context);
        this.workOrderDao = database.workOrderDao();
    }

    public LiveData<List<WorkOrderEntity>> getWorkOrders(@NonNull String token) {
        ApiService api = ApiClient.getApiService(context);
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
        return getWorkOrderById(id, token, null);
    }

    public LiveData<WorkOrderEntity> getWorkOrderById(@NonNull String id, @NonNull String token, ErrorCallback errorCallback) {
        ApiService api = ApiClient.getApiService(context);
        api.getWorkOrderById(id).enqueue(new Callback<WorkOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkOrderResponse> call, @NonNull Response<WorkOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkOrderEntity entity = mapWorkOrder(response.body());
                    executor.execute(() -> workOrderDao.insertAll(Collections.singletonList(entity)));
                    Log.d(TAG, "Fetched work order: " + id);
                } else {
                    Log.e(TAG, "getWorkOrderById failed with code: " + response.code());
                    if (errorCallback != null) errorCallback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkOrderResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getWorkOrderById network error", t);
                if (errorCallback != null) errorCallback.onError("Błąd połączenia z serwerem");
            }
        });
        return workOrderDao.getById(id);
    }

    public LiveData<List<WorkOrderEntity>> getWorkOrdersForRole(@NonNull String token, @NonNull String role, String userId) {
        refreshFromApi(token);
        return getLocalForRole(role, userId);
    }

    public LiveData<List<WorkOrderEntity>> getWorkOrdersForRoleAndStatus(@NonNull String role, String userId, @NonNull String status) {
        switch (role) {
            case "TECHNICIAN":
                return workOrderDao.getByAssignedToAndStatus(userId, status);
            case "OPERATOR":
                return workOrderDao.getByReportedByAndStatus(userId, status);
            default:
                return workOrderDao.getByStatus(status);
        }
    }

    private LiveData<List<WorkOrderEntity>> getLocalForRole(@NonNull String role, String userId) {
        switch (role) {
            case "TECHNICIAN":
                return workOrderDao.getByAssignedTo(userId);
            case "OPERATOR":
                return workOrderDao.getByReportedBy(userId);
            default:
                return workOrderDao.getAll();
        }
    }

    private void refreshFromApi(@NonNull String token) {
        ApiService api = ApiClient.getApiService(context);
        api.getWorkOrders().enqueue(new Callback<List<WorkOrderResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<WorkOrderResponse>> call, @NonNull Response<List<WorkOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkOrderEntity> entities = mapWorkOrders(response.body());
                    executor.execute(() -> database.runInTransaction(() -> {
                        workOrderDao.deleteAll();
                        workOrderDao.insertAll(entities);
                    }));
                    Log.d(TAG, "Refreshed " + entities.size() + " work orders from API");
                } else {
                    Log.e(TAG, "refreshFromApi failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WorkOrderResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "refreshFromApi network error", t);
            }
        });
    }

    public LiveData<List<WorkOrderEntity>> getWorkOrdersByStatus(@NonNull String status) {
        return workOrderDao.getByStatus(status);
    }

    public interface ErrorCallback {
        void onError(String message);
    }

    public interface CreateCallback {
        void onSuccess();
        void onError(String message);
    }

    public void createWorkOrder(@NonNull String token, @NonNull CreateWorkOrderRequest request, @NonNull CreateCallback callback) {
        ApiService api = ApiClient.getApiService(context);
        api.createWorkOrder(request).enqueue(new Callback<WorkOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkOrderResponse> call, @NonNull Response<WorkOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkOrderEntity entity = mapWorkOrder(response.body());
                    executor.execute(() -> workOrderDao.insert(entity));
                    callback.onSuccess();
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkOrderResponse> call, @NonNull Throwable t) {
                callback.onError("Błąd połączenia z serwerem");
            }
        });
    }

    public void updateWorkOrder(@NonNull String token, @NonNull String id, @NonNull UpdateWorkOrderRequest request, @NonNull CreateCallback callback) {
        ApiService api = ApiClient.getApiService(context);
        api.updateWorkOrder(id, request).enqueue(new Callback<WorkOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkOrderResponse> call, @NonNull Response<WorkOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkOrderEntity entity = mapWorkOrder(response.body());
                    executor.execute(() -> workOrderDao.insertAll(Collections.singletonList(entity)));
                    callback.onSuccess();
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkOrderResponse> call, @NonNull Throwable t) {
                callback.onError("Błąd połączenia z serwerem");
            }
        });
    }

    public void updateStatus(@NonNull String token, @NonNull String id, @NonNull String status, @NonNull CreateCallback callback) {
        ApiService api = ApiClient.getApiService(context);
        api.updateWorkOrderStatus(id, new UpdateWorkOrderStatusRequest(status)).enqueue(new Callback<WorkOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkOrderResponse> call, @NonNull Response<WorkOrderResponse> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> workOrderDao.updateStatus(id, status));
                    callback.onSuccess();
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkOrderResponse> call, @NonNull Throwable t) {
                callback.onError("Błąd połączenia z serwerem");
            }
        });
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
                r.getCreatedAt(),
                r.getMachineId(),
                r.getMachine() != null ? r.getMachine().getName() : null,
                r.getAssignedTo() != null ? r.getAssignedTo().getId() : null,
                r.getAssignedTo() != null ? r.getAssignedTo().getName() : null,
                r.getReportedBy() != null ? r.getReportedBy().getId() : null,
                r.getReportedBy() != null ? r.getReportedBy().getName() : null,
                formatParts(r.getParts())
        );
    }

    private String formatParts(java.util.List<WorkOrderResponse.PartUsage> parts) {
        if (parts == null || parts.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            WorkOrderResponse.PartUsage pu = parts.get(i);
            if (pu.getPart() != null) {
                sb.append("• ").append(pu.getPart().getName());
                sb.append(" — ").append(pu.getQuantity()).append(" szt.");
            }
            if (i < parts.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
