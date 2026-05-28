package com.example.cmms.ui.workorders;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.local.entities.WorkOrderEntity;
import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.AddPartRequest;
import com.example.cmms.data.remote.models.AssignTechnicianRequest;
import com.example.cmms.data.remote.models.PartResponse;
import com.example.cmms.data.remote.models.UserResponse;
import com.example.cmms.data.remote.models.WorkOrderPartResponse;
import com.example.cmms.data.remote.models.WorkOrderResponse;
import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.data.repository.WorkOrderRepository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkOrderDetailViewModel extends AndroidViewModel {

    private final WorkOrderRepository workOrderRepository;
    private final AuthRepository authRepository;
    private final Gson gson = new Gson();

    private final MediatorLiveData<WorkOrderEntity> workOrder = new MediatorLiveData<>();
    private LiveData<WorkOrderEntity> workOrderSource;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> statusUpdateSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<List<UserResponse>> technicians = new MutableLiveData<>();
    private final MutableLiveData<List<PartResponse>> availableParts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> assignSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addPartSuccess = new MutableLiveData<>();
    private final MutableLiveData<List<WorkOrderPartResponse>> workOrderParts = new MutableLiveData<>();

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
        workOrderSource = workOrderRepository.getWorkOrderById(id, token, message -> {
            isLoading.postValue(false);
            errorMessage.postValue(message);
        });
        workOrder.addSource(workOrderSource, entity -> {
            if (entity != null) {
                workOrder.setValue(entity);
                isLoading.setValue(false);
            }
        });
    }

    public void changeStatus(@NonNull String id, @NonNull String newStatus) {
        isLoading.setValue(true);
        String token = authRepository.getToken();
        workOrderRepository.updateStatus(token, id, newStatus, new WorkOrderRepository.CreateCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                statusUpdateSuccess.postValue(true);
                loadWorkOrder(id);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void loadTechnicians() {
        ApiService api = ApiClient.getApiService(getApplication());
        api.getUsers("TECHNICIAN").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JsonElement element = gson.fromJson(json, JsonElement.class);
                        JsonArray array;
                        if (element.isJsonArray()) {
                            array = element.getAsJsonArray();
                        } else if (element.isJsonObject()) {
                            JsonObject obj = element.getAsJsonObject();
                            if (obj.has("data")) {
                                array = obj.getAsJsonArray("data");
                            } else {
                                array = new JsonArray();
                            }
                        } else {
                            array = new JsonArray();
                        }
                        List<UserResponse> users = gson.fromJson(array,
                                new TypeToken<List<UserResponse>>(){}.getType());
                        technicians.postValue(users);
                    } catch (Exception e) {
                        errorMessage.postValue("Nie udało się pobrać listy techników");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                errorMessage.postValue("Nie udało się pobrać listy techników");
            }
        });
    }

    public void assignTechnician(@NonNull String workOrderId, @NonNull String technicianId) {
        isLoading.setValue(true);
        ApiService api = ApiClient.getApiService(getApplication());
        api.assignTechnician(workOrderId, new AssignTechnicianRequest(technicianId)).enqueue(new Callback<WorkOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkOrderResponse> call, @NonNull Response<WorkOrderResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    assignSuccess.postValue(true);
                    loadWorkOrder(workOrderId);
                } else {
                    errorMessage.postValue("Nie udało się przypisać technika");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkOrderResponse> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Błąd połączenia z serwerem");
            }
        });
    }

    public void loadAvailableParts() {
        ApiService api = ApiClient.getApiService(getApplication());
        api.getAllParts().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JsonElement element = gson.fromJson(json, JsonElement.class);
                        JsonArray array;
                        if (element.isJsonArray()) {
                            array = element.getAsJsonArray();
                        } else if (element.isJsonObject()) {
                            JsonObject obj = element.getAsJsonObject();
                            if (obj.has("data")) {
                                array = obj.getAsJsonArray("data");
                            } else if (obj.has("parts")) {
                                array = obj.getAsJsonArray("parts");
                            } else {
                                array = new JsonArray();
                            }
                        } else {
                            array = new JsonArray();
                        }
                        List<PartResponse> parts = gson.fromJson(array,
                                new TypeToken<List<PartResponse>>(){}.getType());
                        availableParts.postValue(parts);
                    } catch (Exception e) {
                        errorMessage.postValue("Nie udało się pobrać listy części");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                errorMessage.postValue("Nie udało się pobrać listy części");
            }
        });
    }

    public void loadWorkOrderParts(@NonNull String workOrderId) {
        ApiService api = ApiClient.getApiService(getApplication());
        api.getWorkOrderParts(workOrderId).enqueue(new Callback<List<WorkOrderPartResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<WorkOrderPartResponse>> call, @NonNull Response<List<WorkOrderPartResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    workOrderParts.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WorkOrderPartResponse>> call, @NonNull Throwable t) {
                // silently fail - parts text from entity is already shown
            }
        });
    }

    public void addPart(@NonNull String workOrderId, @NonNull String partId, int quantity) {
        isLoading.setValue(true);
        ApiService api = ApiClient.getApiService(getApplication());
        api.addPartToWorkOrder(workOrderId, new AddPartRequest(partId, quantity)).enqueue(new Callback<WorkOrderPartResponse>() {
            @Override
            public void onResponse(@NonNull Call<WorkOrderPartResponse> call, @NonNull Response<WorkOrderPartResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    addPartSuccess.postValue(true);
                    loadWorkOrder(workOrderId);
                    loadWorkOrderParts(workOrderId);
                    loadAvailableParts();
                } else {
                    errorMessage.postValue("Nie udało się dodać części");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkOrderPartResponse> call, @NonNull Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Błąd połączenia z serwerem");
            }
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

    @NonNull
    public MutableLiveData<Boolean> getStatusUpdateSuccess() {
        return statusUpdateSuccess;
    }

    @NonNull
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @NonNull
    public MutableLiveData<List<UserResponse>> getTechnicians() {
        return technicians;
    }

    @NonNull
    public MutableLiveData<List<PartResponse>> getAvailableParts() {
        return availableParts;
    }

    @NonNull
    public MutableLiveData<Boolean> getAssignSuccess() {
        return assignSuccess;
    }

    @NonNull
    public MutableLiveData<Boolean> getAddPartSuccess() {
        return addPartSuccess;
    }

    @NonNull
    public MutableLiveData<List<WorkOrderPartResponse>> getWorkOrderParts() {
        return workOrderParts;
    }
}
