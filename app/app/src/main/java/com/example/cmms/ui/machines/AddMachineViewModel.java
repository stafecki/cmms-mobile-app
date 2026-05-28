package com.example.cmms.ui.machines;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.CreateMachineRequest;
import com.example.cmms.data.remote.models.LocationResponse;
import com.example.cmms.data.remote.models.MachineResponse;
import com.example.cmms.data.remote.models.UpdateMachineRequest;
import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.data.repository.MachineRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMachineViewModel extends AndroidViewModel {

    private final MachineRepository machineRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<LocationResponse>> locations = new MutableLiveData<>();
    private final MutableLiveData<MachineResponse> machineToEdit = new MutableLiveData<>();

    public AddMachineViewModel(@NonNull Application application) {
        super(application);
        machineRepository = new MachineRepository(application);
        authRepository = new AuthRepository(application);
    }

    public void loadLocations() {
        ApiService api = ApiClient.getApiService(getApplication());
        api.getLocations().enqueue(new Callback<List<LocationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<LocationResponse>> call, @NonNull Response<List<LocationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    locations.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LocationResponse>> call, @NonNull Throwable t) {
                errorMessage.postValue("Nie udało się pobrać lokalizacji");
            }
        });
    }

    public void loadMachineForEdit(@NonNull String machineId) {
        ApiService api = ApiClient.getApiService(getApplication());
        api.getMachineById(machineId).enqueue(new Callback<MachineResponse>() {
            @Override
            public void onResponse(@NonNull Call<MachineResponse> call, @NonNull Response<MachineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    machineToEdit.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MachineResponse> call, @NonNull Throwable t) {
                errorMessage.postValue("Nie udało się pobrać danych maszyny");
            }
        });
    }

    public void saveMachine(@NonNull String name, @NonNull String serialNumber, @NonNull String locationId,
                            double operatingHours, @NonNull String purchaseDate, double purchasePrice) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        String token = authRepository.getToken();
        CreateMachineRequest request = new CreateMachineRequest(
                name, serialNumber, locationId, operatingHours, true, purchaseDate, purchasePrice);

        machineRepository.createMachine(token, request, new MachineRepository.CreateCallback() {
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

    public void updateMachine(@NonNull String id, @NonNull String name, @NonNull String serialNumber,
                              @NonNull String locationId, double operatingHours,
                              @NonNull String purchaseDate, double purchasePrice) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        String token = authRepository.getToken();
        UpdateMachineRequest request = new UpdateMachineRequest(
                name, serialNumber, locationId, operatingHours, purchaseDate, purchasePrice);

        machineRepository.updateMachine(token, id, request, new MachineRepository.CreateCallback() {
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
    public MutableLiveData<List<LocationResponse>> getLocations() { return locations; }

    @NonNull
    public MutableLiveData<MachineResponse> getMachineToEdit() { return machineToEdit; }
}
