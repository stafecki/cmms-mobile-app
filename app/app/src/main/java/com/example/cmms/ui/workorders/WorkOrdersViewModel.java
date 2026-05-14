package com.example.cmms.ui.workorders;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.local.entities.WorkOrderEntity;
import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.data.repository.WorkOrderRepository;

import java.util.List;

public class WorkOrdersViewModel extends AndroidViewModel {

    private final WorkOrderRepository workOrderRepository;
    private final AuthRepository authRepository;

    private final MediatorLiveData<List<WorkOrderEntity>> workOrders = new MediatorLiveData<>();
    private LiveData<List<WorkOrderEntity>> workOrdersSource;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> selectedStatus = new MutableLiveData<>();

    public WorkOrdersViewModel(@NonNull Application application) {
        super(application);
        workOrderRepository = new WorkOrderRepository(application);
        authRepository = new AuthRepository(application);
    }

    public void loadWorkOrders() {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        String token = authRepository.getToken();
        setWorkOrdersSource(workOrderRepository.getWorkOrders(token));
    }

    public void filterByStatus(@NonNull String status) {
        selectedStatus.setValue(status);
        setWorkOrdersSource(workOrderRepository.getWorkOrdersByStatus(status));
    }

    private void setWorkOrdersSource(@NonNull LiveData<List<WorkOrderEntity>> newSource) {
        if (workOrdersSource != null) {
            workOrders.removeSource(workOrdersSource);
        }
        workOrdersSource = newSource;
        workOrders.addSource(workOrdersSource, data -> {
            workOrders.setValue(data);
            isLoading.setValue(false);
        });
    }

    @NonNull
    public LiveData<List<WorkOrderEntity>> getWorkOrders() {
        return workOrders;
    }

    @NonNull
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @NonNull
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @NonNull
    public MutableLiveData<String> getSelectedStatus() {
        return selectedStatus;
    }
}
