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

import com.example.cmms.data.remote.models.CreateMachineRequest;
import com.example.cmms.data.remote.models.UpdateMachineRequest;
import com.example.cmms.data.remote.models.UpdateOperatingHoursRequest;
import com.example.cmms.utils.NetworkUtils;

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

    private final Context context;
    private final AppDatabase database;
    private final MachineDao machineDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public MachineRepository(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(context);
        this.machineDao = database.machineDao();
    }

    public LiveData<List<MachineEntity>> getMachines(@NonNull String token) {
        ApiService api = ApiClient.getApiService(context);
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
        return getMachineById(id, token, null);
    }

    public LiveData<MachineEntity> getMachineById(@NonNull String id, @NonNull String token, ErrorCallback errorCallback) {
        ApiService api = ApiClient.getApiService(context);
        api.getMachineById(id).enqueue(new Callback<MachineResponse>() {
            @Override
            public void onResponse(@NonNull Call<MachineResponse> call, @NonNull Response<MachineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MachineEntity entity = mapMachine(response.body());
                    executor.execute(() -> machineDao.insertAll(Collections.singletonList(entity)));
                    Log.d(TAG, "Fetched machine: " + id);
                } else {
                    Log.e(TAG, "getMachineById failed with code: " + response.code());
                    if (errorCallback != null) errorCallback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MachineResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "getMachineById network error", t);
                if (errorCallback != null) errorCallback.onError("Błąd połączenia z serwerem");
            }
        });
        return machineDao.getById(id);
    }

    public LiveData<List<MachineEntity>> searchMachines(@NonNull String query) {
        return machineDao.search(query);
    }

    public interface ErrorCallback {
        void onError(String message);
    }

    public interface CreateCallback {
        void onSuccess();
        void onError(String message);
    }

    public void createMachine(@NonNull String token, @NonNull CreateMachineRequest request, @NonNull CreateCallback callback) {
        ApiService api = ApiClient.getApiService(context);
        api.createMachine(request).enqueue(new Callback<MachineResponse>() {
            @Override
            public void onResponse(@NonNull Call<MachineResponse> call, @NonNull Response<MachineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MachineEntity entity = mapMachine(response.body());
                    executor.execute(() -> machineDao.insert(entity));
                    callback.onSuccess();
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MachineResponse> call, @NonNull Throwable t) {
                callback.onError("Błąd połączenia z serwerem");
            }
        });
    }

    public void updateMachine(@NonNull String token, @NonNull String id, @NonNull UpdateMachineRequest request, @NonNull CreateCallback callback) {
        ApiService api = ApiClient.getApiService(context);
        api.updateMachine(id, request).enqueue(new Callback<MachineResponse>() {
            @Override
            public void onResponse(@NonNull Call<MachineResponse> call, @NonNull Response<MachineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MachineEntity entity = mapMachine(response.body());
                    executor.execute(() -> machineDao.insert(entity));
                    callback.onSuccess();
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MachineResponse> call, @NonNull Throwable t) {
                callback.onError("Błąd połączenia z serwerem");
            }
        });
    }

    public void updateOperatingHours(@NonNull String token, @NonNull String id, double hours, @NonNull CreateCallback callback) {
        ApiService api = ApiClient.getApiService(context);
        api.updateOperatingHours(id, new UpdateOperatingHoursRequest(hours)).enqueue(new Callback<MachineResponse>() {
            @Override
            public void onResponse(@NonNull Call<MachineResponse> call, @NonNull Response<MachineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MachineEntity entity = mapMachine(response.body());
                    executor.execute(() -> machineDao.insert(entity));
                    callback.onSuccess();
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MachineResponse> call, @NonNull Throwable t) {
                callback.onError("Błąd połączenia z serwerem");
            }
        });
    }

    public void deleteMachine(@NonNull String token, @NonNull String id, @NonNull CreateCallback callback) {
        ApiService api = ApiClient.getApiService(context);
        api.deleteMachine(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> machineDao.deleteById(id));
                    callback.onSuccess();
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Błąd połączenia z serwerem");
            }
        });
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
                r.isActive(),
                r.getLocationId(),
                r.getLocation() != null ? r.getLocation().getName() : null,
                r.getPurchaseDate(),
                r.getPurchasePrice()
        );
    }
}
