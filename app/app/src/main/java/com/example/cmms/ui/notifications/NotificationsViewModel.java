package com.example.cmms.ui.notifications;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.local.entities.NotificationEntity;
import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.data.repository.NotificationRepository;

import java.util.List;

public class NotificationsViewModel extends AndroidViewModel {

    private final NotificationRepository notificationRepository;
    private final AuthRepository authRepository;

    private final MediatorLiveData<List<NotificationEntity>> notifications = new MediatorLiveData<>();
    private LiveData<List<NotificationEntity>> notificationsSource;
    private final LiveData<Integer> unreadCount;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public NotificationsViewModel(@NonNull Application application) {
        super(application);
        notificationRepository = new NotificationRepository(application);
        authRepository = new AuthRepository(application);
        unreadCount = notificationRepository.getUnreadCount();
    }

    public void loadNotifications() {
        isLoading.setValue(true);
        String token = authRepository.getToken();

        if (notificationsSource != null) {
            notifications.removeSource(notificationsSource);
        }
        notificationsSource = notificationRepository.getNotifications(token);
        notifications.addSource(notificationsSource, data -> {
            notifications.setValue(data);
            isLoading.setValue(false);
        });
    }

    @NonNull
    public LiveData<List<NotificationEntity>> getNotifications() {
        return notifications;
    }

    @NonNull
    public LiveData<Integer> getUnreadCount() {
        return unreadCount;
    }

    @NonNull
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
