package com.example.cmms.ui.workorders;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.CreateWorkOrderRequest;
import com.example.cmms.data.remote.models.MachineResponse;
import com.example.cmms.data.remote.models.UpdateWorkOrderRequest;
import com.example.cmms.data.remote.models.WorkOrderResponse;
import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.data.repository.WorkOrderRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWorkOrderViewModel extends AndroidViewModel {

    private final WorkOrderRepository workOrderRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<MachineResponse>> machines = new MutableLiveData<>();
    private final MutableLiveData<WorkOrderResponse> workOrderToEdit = new MutableLiveData<>();

    public AddWorkOrderViewModel(@NonNull Application application) {
        super(application);
        workOrderRepository = new WorkOrderRepository(application);
        authRepository = new AuthRepository(application);
    }

    public void loadMachines() {
        ApiService api = ApiClient.getApiService(getApplication());
        api.getMachines().enqueue(new Callback<List<MachineResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<MachineResponse>> call, @NonNull Response<List<MachineResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    machines.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineResponse>> call, @NonNull Throwable t) {
                errorMessage.postValue("Nie udało się pobrać listy maszyn");
            }
        });
    }

    public void loadWorkOrderForEdit(@NonNull String workOrderId) {
        ApiService api = ApiClient.getApiService(getApplication());
        api.getWorkOrderById(workOrderId).enqueue(new Callback<WorkOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkOrderResponse> call, @NonNull Response<WorkOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    workOrderToEdit.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkOrderResponse> call, @NonNull Throwable t) {
                errorMessage.postValue("Nie udało się pobrać danych zlecenia");
            }
        });
    }

    public void saveWorkOrder(@NonNull String machineId, @NonNull String title,
                              @NonNull String description, @NonNull String priority) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        String token = authRepository.getToken();
        CreateWorkOrderRequest request = new CreateWorkOrderRequest(machineId, title, description, priority);

        workOrderRepository.createWorkOrder(token, request, new WorkOrderRepository.CreateCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void updateWorkOrder(@NonNull String id, @NonNull String title,
                                @NonNull String description, @NonNull String priority) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        String token = authRepository.getToken();
        UpdateWorkOrderRequest request = new UpdateWorkOrderRequest(title, description, priority);

        workOrderRepository.updateWorkOrder(token, id, request, new WorkOrderRepository.CreateCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    @NonNull
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }

    @NonNull
    public MutableLiveData<Boolean> getSaveSuccess() { return saveSuccess; }

    @NonNull
    public MutableLiveData<String> getErrorMessage() { return errorMessage; }

    @NonNull
    public MutableLiveData<List<MachineResponse>> getMachines() { return machines; }

    @NonNull
    public MutableLiveData<WorkOrderResponse> getWorkOrderToEdit() { return workOrderToEdit; }
}
