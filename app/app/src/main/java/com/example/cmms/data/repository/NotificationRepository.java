package com.example.cmms.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.cmms.data.local.AppDatabase;
import com.example.cmms.data.local.dao.NotificationDao;
import com.example.cmms.data.local.entities.NotificationEntity;
import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.NotificationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {

    private static final String TAG = "NotificationRepository";

    private final AppDatabase database;
    private final NotificationDao notificationDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public NotificationRepository(@NonNull Context context) {
        this.database = AppDatabase.getInstance(context);
        this.notificationDao = database.notificationDao();
    }

    public LiveData<List<NotificationEntity>> getNotifications(@NonNull String token) {
        ApiService api = ApiClient.getApiService(token);
        api.getNotifications().enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<NotificationResponse>> call, @NonNull Response<List<NotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationEntity> entities = mapNotifications(response.body());
                    executor.execute(() -> database.runInTransaction(() -> {
                        notificationDao.deleteAll();
                        notificationDao.insertAll(entities);
                    }));
                    Log.d(TAG, "Fetched " + entities.size() + " notifications from API");
                } else {
                    Log.e(TAG, "getNotifications failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NotificationResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "getNotifications network error", t);
            }
        });
        return notificationDao.getAll();
    }

    public LiveData<Integer> getUnreadCount() {
        return notificationDao.getUnreadCount();
    }

    private List<NotificationEntity> mapNotifications(List<NotificationResponse> responses) {
        List<NotificationEntity> entities = new ArrayList<>(responses.size());
        for (NotificationResponse r : responses) {
            entities.add(mapNotification(r));
        }
        return entities;
    }

    private NotificationEntity mapNotification(NotificationResponse r) {
        return new NotificationEntity(
                r.getId(),
                r.getType(),
                r.getTitle(),
                r.getMessage(),
                r.isRead(),
                r.getCreatedAt()
        );
    }
}
