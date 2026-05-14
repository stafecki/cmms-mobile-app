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

public class WorkOrderDetailViewModel extends AndroidViewModel {

    private final WorkOrderRepository workOrderRepository;
    private final AuthRepository authRepository;

    private final MediatorLiveData<WorkOrderEntity> workOrder = new MediatorLiveData<>();
    private LiveData<WorkOrderEntity> workOrderSource;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public WorkOrderDetailViewModel(@NonNull Application application) {
        super(application);
        workOrderRepository = new WorkOrderRepository(application);
        authRepository = new AuthRepository(application);
    }

    public void loadWorkOrder(@NonNull String id) {
        isLoading.setValue(true);
        String token = authRepository.getToken();

        if (workOrderSource != null) {
            workOrder.removeSource(workOrderSource);
        }
        workOrderSource = workOrderRepository.getWorkOrderById(id, token);
        workOrder.addSource(workOrderSource, data -> {
            workOrder.setValue(data);
            isLoading.setValue(false);
        });
    }

    @NonNull
    public LiveData<WorkOrderEntity> getWorkOrder() {
        return workOrder;
    }

    @NonNull
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
